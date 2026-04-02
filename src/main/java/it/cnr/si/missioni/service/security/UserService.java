package it.cnr.si.missioni.service.security;

import it.cnr.si.missioni.domain.custom.Authority;
import it.cnr.si.missioni.domain.custom.User;
import it.cnr.si.missioni.repository.AuthorityRepository;
import it.cnr.si.missioni.repository.UserRepository;
import it.cnr.si.missioni.util.RandomUtil;
import it.cnr.si.missioni.web.rest.vm.ManagedUserVM;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Service class for managing users.
 */
@Service
@Transactional
@Profile("!keycloak")
public class UserService implements InitializingBean {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    @Inject
    private PasswordEncoder passwordEncoder;

    @Inject
    private UserRepository userRepository;

    @Inject
    private AuthorityRepository authorityRepository;

    @Autowired(required = false)
    private SecurityService securityService;

    public Optional<User> activateRegistration(String key) {
        log.debug("Activating user for activation key {}", key);
        return userRepository.findOneByActivationKey(key)
                .map(user -> {
                    user.setActivated(true);
                    user.setActivationKey(null);
                    userRepository.save(user);
                    log.debug("Activated user: {}", user);
                    return user;
                });
    }

    public Optional<User> completePasswordReset(String newPassword, String key) {
        log.debug("Reset user password for reset key {}", key);

        return userRepository.findOneByResetKey(key)
                .filter(user -> {
                    ZonedDateTime oneDayAgo = ZonedDateTime.now().minusHours(24);
                    return user.getResetDate() != null && user.getResetDate().isAfter(oneDayAgo);
                })
                .map(user -> {
                    user.setPassword(passwordEncoder.encode(newPassword));
                    user.setResetKey(null);
                    user.setResetDate(null);
                    userRepository.save(user);
                    return user;
                });
    }

    public Optional<User> requestPasswordReset(String mail) {
        return userRepository.findOneByEmail(mail)
                .filter(User::getActivated)
                .map(user -> {
                    user.setResetKey(RandomUtil.generateResetKey());
                    user.setResetDate(ZonedDateTime.now());
                    userRepository.save(user);
                    return user;
                });
    }

    public User createUser(String login, String password, String firstName, String lastName, String email,
                           String langKey) {

        User newUser = new User();
        Authority authority = authorityRepository.findById(AuthoritiesConstants.USER).orElse(null);
        Set<Authority> authorities = new HashSet<>();

        String encryptedPassword = passwordEncoder.encode(password);
        newUser.setLogin(login);
        newUser.setPassword(encryptedPassword);
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setEmail(email);
        newUser.setLangKey(langKey);
        newUser.setActivated(false);
        newUser.setActivationKey(RandomUtil.generateActivationKey());

        if (authority != null) {
            authorities.add(authority);
        }
        newUser.setAuthorities(authorities);

        if (securityService != null) {
            newUser.setCreatedBy(securityService.getCurrentUserLogin());
        }
        newUser.setCreatedDate(ZonedDateTime.now());

        userRepository.save(newUser);
        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }

    public User createUser(ManagedUserVM managedUserVM) {
        User user = new User();
        user.setLogin(managedUserVM.getLogin());
        user.setFirstName(managedUserVM.getFirstName());
        user.setLastName(managedUserVM.getLastName());
        user.setEmail(managedUserVM.getEmail());
        user.setLangKey(managedUserVM.getLangKey() == null ? "en" : managedUserVM.getLangKey());

        if (managedUserVM.getAuthorities() != null) {
            Set<Authority> authorities = new HashSet<>();
            managedUserVM.getAuthorities().forEach(
                    authority -> {
                        Authority found = authorityRepository.findById(authority).orElse(null);
                        if (found != null) {
                            authorities.add(found);
                        }
                    }
            );
            user.setAuthorities(authorities);
        }

        String encryptedPassword = passwordEncoder.encode(RandomUtil.generatePassword());
        user.setPassword(encryptedPassword);
        user.setResetKey(RandomUtil.generateResetKey());
        user.setResetDate(ZonedDateTime.now());
        user.setActivated(true);

        userRepository.save(user);
        log.debug("Created Information for User: {}", user);
        return user;
    }

    public void updateUser(String firstName, String lastName, String email, String langKey) {
        if (securityService == null) {
            log.warn("SecurityService is not available");
            return;
        }

        userRepository.findOneByLogin(securityService.getCurrentUserLogin()).ifPresent(u -> {
            u.setFirstName(firstName);
            u.setLastName(lastName);
            u.setEmail(email);
            u.setLangKey(langKey);
            userRepository.save(u);
            log.debug("Changed Information for User: {}", u);
        });
    }

    public void updateUser(Long id, String login, String firstName, String lastName, String email,
                           boolean activated, String langKey, Set<String> authorities) {

        userRepository.findOneById(id).ifPresent(u -> {
            u.setLogin(login);
            u.setFirstName(firstName);
            u.setLastName(lastName);
            u.setEmail(email);
            u.setActivated(activated);
            u.setLangKey(langKey);

            Set<Authority> managedAuthorities = u.getAuthorities();
            managedAuthorities.clear();

            if (authorities != null) {
                authorities.forEach(authority -> {
                    Authority found = authorityRepository.findById(authority).orElse(null);
                    if (found != null) {
                        managedAuthorities.add(found);
                    }
                });
            }

            log.debug("Changed Information for User: {}", u);
        });
    }

    public void deleteUser(String login) {
        userRepository.findOneByLogin(login).ifPresent(u -> {
            userRepository.delete(u);
            log.debug("Deleted User: {}", u);
        });
    }

    public void changePassword(String password) {
        if (securityService == null) {
            log.warn("SecurityService is not available");
            return;
        }

        userRepository.findOneByLogin(securityService.getCurrentUserLogin()).ifPresent(u -> {
            String encryptedPassword = passwordEncoder.encode(password);
            u.setPassword(encryptedPassword);
            userRepository.save(u);
            log.debug("Changed password for User: {}", u);
        });
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserWithAuthoritiesByLogin(String login) {
        return userRepository.findOneByLogin(login).map(u -> {
            u.getAuthorities().size();
            return u;
        });
    }

    @Transactional(readOnly = true)
    public User getUserWithAuthorities(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id:" + id));
        user.getAuthorities().size();
        return user;
    }

    @Transactional(readOnly = true)
    public User getUserWithAuthorities() {
        if (securityService == null) {
            throw new RuntimeException("SecurityService is not available");
        }

        User user = userRepository.findOneByLogin(securityService.getCurrentUserLogin())
                .orElseThrow(() -> new RuntimeException("Current user not found"));
        user.getAuthorities().size();
        return user;
    }

    /**
     * Not activated users should be automatically deleted after 3 days.
     * This is scheduled to get fired everyday, at 01:00 (am).
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void removeNotActivatedUsers() {
        ZonedDateTime now = ZonedDateTime.now();
        List<User> users = userRepository.findAllByActivatedIsFalseAndCreatedDateBefore(now.minusDays(3));
        for (User user : users) {
            log.debug("Deleting not activated user {}", user.getLogin());
            userRepository.delete(user);
        }
    }

    @Override
    public void afterPropertiesSet() {
        log.info("UserService initialized without legacy JdbcTokenStore");
    }
}