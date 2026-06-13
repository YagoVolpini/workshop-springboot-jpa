package com.example.demo.services;

import com.example.demo.dto.PasswordUpdateDTO;
import com.example.demo.dto.UserDTO;
import com.example.demo.dto.UserInsertDTO;
import com.example.demo.entities.Role;
import com.example.demo.entities.User;
import com.example.demo.repositories.RoleRepository;
import com.example.demo.repositories.UserRepository;
import com.example.demo.services.exceptions.*;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;


    @Transactional(readOnly = true)
    public Page<UserDTO> findAll(Pageable pageable) {
        User user = getAuthenticatedUser();
        boolean isAdmin = checkIsAdmin(user);

        if (isAdmin) {
            return userRepository.findAll(pageable).map(UserDTO::new);
        } else {
            return userRepository.findById(user.getId(), pageable).map(UserDTO::new);
        }
    }

    @Transactional(readOnly = true)
    public UserDTO findById(Long id) {
        User user = getAuthenticatedUser();
        validateSelfOrAdmin(id, user);
        User entity = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Resource not found with id " + id));
        return new UserDTO(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    public UserDTO insert(UserInsertDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new AlreadyExistsException(String.format("Email %s already exists", dto.getEmail()));
        }

        User entity = new User();
        updateData(dto, entity);
        entity.setPassword(passwordEncoder.encode(dto.getPassword()));
        Role clientRole = roleRepository.findByAuthority("ROLE_CLIENT")
                .orElseThrow(() -> new ResourceNotFoundException("Role ROLE_CLIENT not found in database"));

        entity.getRoles().add(clientRole);
        entity = userRepository.save(entity);
        return new UserDTO(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        User user = getAuthenticatedUser();
        validateSelfOrAdmin(id, user);
        User entity = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found with id " + id));
        try {
            userRepository.delete(entity);
            userRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Cannot delete this resource because it is associated with other records");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public UserDTO update(Long id, UserDTO dto) {
        User user = getAuthenticatedUser();
        validateSelfOrAdmin(id, user);
        User entity = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Resource not found with id " + id));
        updateData(dto, entity);
        entity = userRepository.save(entity);
        return new UserDTO(entity);
    }

    private void updateData(UserDTO dto, User entity) {
        if (dto.getName() != null) entity.setName(dto.getName());
        if (dto.getEmail() != null) entity.setEmail(dto.getEmail());
        if (dto.getPhone() != null) entity.setPhone(dto.getPhone());
    }

    @Transactional(rollbackFor = Exception.class)
    public void updatePassword(Long id, PasswordUpdateDTO dto) {
        User user = getAuthenticatedUser();
        validateSelfOrAdmin(id, user);
        User entity = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Resource not found with id " + id));
        entity.setPassword(passwordEncoder.encode(dto.getPassword()));
        userRepository.save(entity);
    }

    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName().equals("anonymousUser")) {
            throw new BusinessException("User is not logged in or token is invalid");
        }

        String email = authentication.getName();

        return userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("Resource not found with " + email));
    }

    private void validateSelfOrAdmin(Long id, User user) {


        if (!checkIsAdmin(user) && !user.getId().equals(id)) {
            throw new ForbiddenException("Access denied. You can only access your own data.");
        }
    }

    private boolean checkIsAdmin(User user) {
        return user.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"));
    }
}
