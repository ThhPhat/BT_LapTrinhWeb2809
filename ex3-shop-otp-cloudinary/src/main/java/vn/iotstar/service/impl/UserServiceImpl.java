package vn.iotstar.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.iotstar.dto.UserDTO;
import vn.iotstar.entity.Role;
import vn.iotstar.entity.User;
import vn.iotstar.mapper.UserMapper;
import vn.iotstar.repository.ProductRepository;
import vn.iotstar.repository.RoleRepository;
import vn.iotstar.repository.UserRepository;
import vn.iotstar.service.UserService;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    static final String DEFAULT_PASSWORD = "123456";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ProductRepository productRepository;
    private final UserMapper mapper;
    private final PasswordEncoder passwordEncoder;

    private UserDTO toDto(User u) {
        UserDTO dto = mapper.toDTO(u);
        dto.setProductCount(productRepository.countByUserId(u.getId()));
        return dto;
    }

    private User load(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User không tồn tại"));
    }

    private Role role(String name) {
        return roleRepository.findByName(name == null || name.isBlank() ? "ROLE_USER" : name)
                .orElseThrow(() -> new IllegalArgumentException("Role không tồn tại"));
    }

    @Override @Transactional(readOnly = true)
    public Page<UserDTO> findAll(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1),
                Sort.by(Sort.Direction.DESC, "id"));
        return userRepository.search(keyword == null ? "" : keyword.trim(), pageable).map(this::toDto);
    }

    @Override @Transactional(readOnly = true)
    public UserDTO findById(Long id) { return toDto(load(id)); }

    @Override @Transactional
    public UserDTO create(UserDTO dto) {
        if (userRepository.existsByUsername(dto.getUsername()))
            throw new IllegalArgumentException("Username đã tồn tại");
        if (userRepository.existsByEmail(dto.getEmail()))
            throw new IllegalArgumentException("Email đã tồn tại");
        User user = mapper.toEntity(dto);
        user.setId(null);
        user.setRole(role(dto.getRoleName()));
        user.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
        user.setEnabled(dto.isEnabled());
        return toDto(userRepository.save(user));
    }

    @Override @Transactional
    public UserDTO update(Long id, UserDTO dto) {
        User user = load(id);
        if (userRepository.existsByUsernameAndIdNot(dto.getUsername(), id))
            throw new IllegalArgumentException("Username đã tồn tại");
        if (userRepository.existsByEmailAndIdNot(dto.getEmail(), id))
            throw new IllegalArgumentException("Email đã tồn tại");
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setFullName(dto.getFullName());
        user.setEnabled(dto.isEnabled());
        if (dto.getRoleName() != null && !dto.getRoleName().isBlank())
            user.setRole(role(dto.getRoleName()));
        return toDto(userRepository.save(user));
    }

    @Override @Transactional
    public void delete(Long id, Long currentUserId) {
        if (id.equals(currentUserId))
            throw new IllegalArgumentException("Không thể tự xóa tài khoản đang đăng nhập");
        User user = load(id);
        if (productRepository.countByUserId(id) > 0)
            throw new IllegalArgumentException("User còn sản phẩm, hãy xóa sản phẩm trước");
        userRepository.delete(user);
    }

    @Override @Transactional(readOnly = true)
    public long countUsers() { return userRepository.count(); }

    @Override @Transactional(readOnly = true)
    public long countProducts(Long userId) { return productRepository.countByUserId(userId); }
}
