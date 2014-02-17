package com.odobo.grails.plugin.springsecurity.rest.oauth

import groovy.util.logging.Log4j
import org.pac4j.core.profile.UserProfile
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException

/**
 * Builds an {@link OauthUser}. Delegates to the default {@link UserDetailsService#loadUserByUsername(java.lang.String)}
 * where the username passed is {@link UserProfile#getId()}.
 */
@Log4j
class DefaultOauthUserDetailsService implements OauthUserDetailsService {

    @Delegate
    UserDetailsService userDetailsService

    OauthUser loadUserByUserProfile(UserProfile userProfile, Collection<GrantedAuthority> defaultRoles) {
        UserDetails userDetails
        OauthUser oauthUser

        try {
            log.debug "Trying to fetch user details for user profile: ${userProfile}"
            userDetails = userDetailsService.loadUserByUsername userProfile.id
            Collection<GrantedAuthority> allRoles = userDetails.authorities + defaultRoles
            oauthUser = new OauthUser(userDetails.username, userDetails.password, allRoles, userProfile)
        } catch (UsernameNotFoundException unfe) {
            log.debug "User not found. Creating a new one with default roles: ${defaultRoles}"
            oauthUser = new OauthUser(userProfile.id, 'N/A', defaultRoles, userProfile)
        }
        return oauthUser
    }


}
