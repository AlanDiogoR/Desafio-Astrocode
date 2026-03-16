package com.astrocode.backend.domain.PlanAccess;

import com.astrocode.backend.domain.entities.User;
import com.astrocode.backend.domain.exceptions.PlanUpgradeRequiredException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.security.access.AccessDeniedException;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PlanAccessAspect {

    @Around("@annotation(requiresPro)")
    public Object checkPlanAccess(ProceedingJoinPoint joinPoint, RequiresPro requiresPro) throws Throwable {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof User user)) {
            throw new AccessDeniedException("Autenticação necessária para acessar este recurso");
        }

        if (!user.isPro()) {
            throw new PlanUpgradeRequiredException(requiresPro.message(), "pro_feature");
        }

        return joinPoint.proceed();
    }
}
