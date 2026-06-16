package com.example.taxi_project.security;



import com.example.taxi_project.enums.UserRole;
import com.example.taxi_project.model.Driver;
import com.example.taxi_project.model.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * CustomUserDetails — Spring Security uchun universal wrapper.
 *
 * Ikki turdagi foydalanuvchini qo'llab-quvvatlaydi:
 *   - Users  (ota-ona, PARENT / ADMIN)
 *   - Child  (bola, CHILD)
 *
 * Controller da ishlatish:
 *
 *   @AuthenticationPrincipal CustomUserDetails userDetails
 *
 *   userDetails.getUsers()   → Users  obyektini olish (null bo'lishi mumkin)
 *   userDetails.getChild()   → Child  obyektini olish (null bo'lishi mumkin)
 *   userDetails.isParent()   → ota-ona ekanligini tekshirish
 *   userDetails.isChild()    → bola ekanligini tekshirish
 *   userDetails.getId()      → UUID (kim bo'lishidan qat'i nazar)
 */
public class CustomUserDetails implements UserDetails {

    @Getter
    private final User user;

    @Getter
    private final Driver driver;

    public CustomUserDetails(User user, Driver driver) {
        this.user = user;
        this.driver = driver;
    }

    public CustomUserDetails(Driver driver) {
        this.driver = driver;
        this.user = null;
    }

    public UUID getId() {
        return isParent() ? user.getId() : driver.getId();
    }

    public String getFullName() {
        return isParent() ? user.getName() : driver.getName();
    }

    public boolean isParent() {
        return user != null;
    }

    public boolean isDriver() {
        return driver != null;
    }

    public UserRole getRole() {
        if (isParent()) return user.getRole();
        return UserRole.DRIVER;
    }

    @Override
    public String getUsername() {
         return isParent() ? user.getPhone() : driver.getPhone();
    }

    @Override
    public String getPassword() {
         return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String role = isParent() ? user.getRole().name() : UserRole.DRIVER.name();
        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() {
        // ✅ to'g'rilandi — modelda isActive bo'lsa shu, bo'lmasa true
        return isParent() ? user.is_active() : driver.is_active();
    }
}