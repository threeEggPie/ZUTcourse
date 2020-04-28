package xyz.kingsword.course.util;

import java.util.Collection;
import java.util.function.Supplier;

public class ConditionUtil {
    private static ConditionUtil rightCondition = new ConditionUtil(true);
    private static ConditionUtil wrongCondition = new ConditionUtil(false);
    private final Boolean flag;

    private ConditionUtil(Boolean flag) {
        this.flag = flag;
    }

    public static ConditionUtil validateTrue(Boolean flag) {
        return new ConditionUtil(flag);
    }

    public static ConditionUtil notNull(Object... objs) {
        for (Object o : objs) {
            if (o == null) {
                return wrongCondition;
            }
        }
        return rightCondition;
    }

    public static ConditionUtil notEmpty(Collection... objs) {
        for (Collection o : objs) {
            if (o == null || o.isEmpty()) {
                return wrongCondition;
            }
        }
        return rightCondition;
    }

    public <X extends Throwable> void orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        if (flag == null || !flag)
            throw exceptionSupplier.get();
    }
}
