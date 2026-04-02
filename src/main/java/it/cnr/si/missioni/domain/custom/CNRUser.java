package it.cnr.si.missioni.domain.custom;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;

/**
 * Created by francesco on 02/04/15.
 */
public class CNRUser implements UserDetails, Serializable {

    private String username;

    private String password;

    private String matricola;

    private String email;

    private boolean expired;

    private boolean enabled;

    private String departmentNumber;

    private String lastName;

    private String firstName;

    private String livello;


    private Collection<? extends GrantedAuthority> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public String getMatricola(){
        return matricola;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setMatricola(String matricola) {
        this.matricola = matricola;
    }

    public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepartmentNumber() {
        return departmentNumber;
    }

    public void setDepartmentNumber(String departmentNumber) {
        this.departmentNumber = departmentNumber;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLivello() {
        return livello;
    }

    public void setLivello(String livello) {
        this.livello = livello;
    }

    @Override
    public String toString() {
        return "CNRUser{" +
                "username='" + username + '\'' +
                ", matricola='" + matricola + '\'' +
                ", email='" + email + '\'' +
                ", expired=" + expired +
                ", enabled=" + enabled +
                ", authorities=" + authorities +
                ", departmentNumber=" + departmentNumber +
                ", firstName=" + firstName +
                ", lastName=" + lastName +
                ", livello=" + livello +
                '}';
    }

}