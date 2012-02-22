/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.utils.annotation;

@java.lang.annotation.Documented
@java.lang.annotation.Target({java.lang.annotation.ElementType.TYPE})
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.CLASS)

/**
 * Indicates that a type is immutable.
 *
 * @author Jakub D Kozlowski
 * @since 0.6
 *
 * @see javax.annotation.concurrent.Immutable
 */
public @interface Immutable {
}
