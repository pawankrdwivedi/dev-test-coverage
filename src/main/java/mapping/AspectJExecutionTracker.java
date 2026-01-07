package mapping;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.JoinPoint;

@Aspect
public class AspectJExecutionTracker {

    @Before("execution(* com.iris.automation.app..*(..))")
    public void pointcutInsideDevMethods(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        System.out.println(
                "Executing method inside class.method: " + className + "." + methodName
        );
        ExecutionTracker.logMethod(className, methodName);
    }
}
