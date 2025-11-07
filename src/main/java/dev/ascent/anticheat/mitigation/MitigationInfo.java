package dev.ascent.anticheat.mitigation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/** Optional metadata for docs/UX. */
@Retention(RetentionPolicy.RUNTIME)
public @interface MitigationInfo {
    String key();
    String desc() default "";
}
