package com.movies_series_watch.auth.config;

import com.movies_series_watch.auth.user.Role;
import com.movies_series_watch.auth.user.RoleRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class RoleInitializer implements ApplicationRunner {

    private final RoleRepository roleRepository;

    public RoleInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        List<String> roles = List.of("END_USER", "PRODUCT_OWNER");
        for (String roleName : roles) {
            roleRepository.findByName(roleName)
                    .orElseGet(() -> roleRepository.save(new Role(roleName)));
        }
    }
}