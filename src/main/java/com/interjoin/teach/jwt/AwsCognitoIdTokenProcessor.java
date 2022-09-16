package com.interjoin.teach.jwt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
    public class AwsCognitoIdTokenProcessor {

    private final JwtConfiguration jwtConfiguration;
    private final ConfigurableJWTProcessor configurableJWTProcessor;

    public Authentication authenticate(HttpServletRequest request) throws Exception {
        String idToken = request.getHeader(this.jwtConfiguration.getHttpHeader());
        if (idToken != null) {

            JWTClaimsSet claims = getClaimsFromToken(idToken);
            validateIssuer(claims);
            verifyIfIdToken(claims);
            String username = getUserNameFromClaims(claims);
            if (username != null) {
                List<String> roles = getUserRoles(claims);

                roles = roles.stream()
                             .map(role -> "ROLE_" + role)
                             .collect(Collectors.toList());

                List<GrantedAuthority> grantedAuthorities = AuthorityUtils.commaSeparatedStringToAuthorityList(String.join(",", roles));

                User user = new User(username, "", grantedAuthorities);
                return new JwtAuthentication(user, claims, grantedAuthorities);
            }
        }
        return null;
    }

    public Date getExpireAtFromToken(String token) throws BadJOSEException, ParseException, JOSEException {
        String actualToken = getBearerToken(token);
        JWTClaimsSet claims = getClaimsFromToken(actualToken);
        return (Date)claims.getClaims().get("exp");
    }

    private JWTClaimsSet getClaimsFromToken(String accessToken) throws BadJOSEException, ParseException, JOSEException {
        return this.configurableJWTProcessor.process(this.getBearerToken(accessToken),null);
    }

    private String getUserNameFromClaims(JWTClaimsSet claims) {
        return claims.getClaims().get(this.jwtConfiguration.getUserNameField()).toString();
    }
    public String getUserNameFromToken(String token) throws BadJOSEException, ParseException, JOSEException {
        return getClaimsFromToken(token).getClaims().get(this.jwtConfiguration.getUserNameField()).toString();
    }

    private List<String> getUserRoles(JWTClaimsSet claims) {
        return (List<String>) claims.getClaims().get(this.jwtConfiguration.getGroupsField());
    }

    private void verifyIfIdToken(JWTClaimsSet claims) throws Exception {
        if (!claims.getIssuer().equals(this.jwtConfiguration.getCognitoIdentityPoolUrl())) {
            throw new Exception("JWT Token is not an ID Token");
        }
    }

    private void validateIssuer(JWTClaimsSet claims) throws Exception {
        if (!claims.getIssuer().equals(this.jwtConfiguration.getCognitoIdentityPoolUrl())) {
            throw new Exception(String.format("Issuer %s does not match cognito idp %s", claims.getIssuer(), this.jwtConfiguration.getCognitoIdentityPoolUrl()));
        }
    }

    private String getBearerToken(String token) {
        return token.startsWith("Bearer ") ? token.substring("Bearer ".length()) : token;
    }
}