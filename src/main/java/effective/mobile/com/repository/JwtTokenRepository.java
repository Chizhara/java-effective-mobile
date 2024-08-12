package effective.mobile.com.repository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.DeferredCsrfToken;
import org.springframework.stereotype.Repository;

@Repository
public class JwtTokenRepository implements CsrfTokenRepository {
    @Override
    public CsrfToken generateToken(HttpServletRequest request) {
        return null;
    }

    @Override
    public void saveToken(CsrfToken token, HttpServletRequest request, HttpServletResponse response) {

    }

    @Override
    public CsrfToken loadToken(HttpServletRequest request) {
        return null;
    }

    @Override
    public DeferredCsrfToken loadDeferredToken(HttpServletRequest request, HttpServletResponse response) {
        return CsrfTokenRepository.super.loadDeferredToken(request, response);
    }
}
