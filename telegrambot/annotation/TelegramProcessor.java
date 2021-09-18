package com.pds.telegrambot.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.pds.telegrambot.StateTypes;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TelegramProcessor {
    String[] commands() default {};
    String[] from_states() default {};
    String success() default "";
    String fail() default "";
    String state_type() default StateTypes.Keep;
    boolean run_only() default false;
}
