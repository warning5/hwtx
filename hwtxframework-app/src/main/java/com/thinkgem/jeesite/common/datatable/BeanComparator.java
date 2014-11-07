package com.thinkgem.jeesite.common.datatable;

import com.esotericsoftware.reflectasm.MethodAccess;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.collections.comparators.ComparableComparator;

import java.io.Serializable;
import java.util.Comparator;

/**
 * <p>
 * This comparator compares two beans by the specified bean property. It is also
 * possible to compare beans based on nested, indexed, combined, mapped bean
 * properties. Please see the {@link PropertyUtilsBean} documentation for all
 * property name possibilities.
 * <p/>
 * </p>
 * <p>
 * <strong>Note:</strong> The BeanComparator passes the values of the specified
 * bean property to a ComparableComparator, if no comparator is specified in the
 * constructor. If you are comparing two beans based on a property that could
 * contain "null" values, a suitable <code>Comparator</code> or
 * <code>ComparatorChain</code> should be supplied in the constructor.
 * </p>
 *
 * @author <a href"mailto:epugh@upstate.com">Eric Pugh</a>
 * @author Tim O'Brien
 */
public class BeanComparator implements Comparator, Serializable {

    private String property;
    private Comparator comparator;

    /**
     * <p>
     * Constructs a Bean Comparator without a property set.
     * </p>
     * <p>
     * <strong>Note</strong> that this is intended to be used only in
     * bean-centric environments.
     * </p>
     * <p>
     * Until {@link #setProperty} is called with a non-null value. this
     * comparator will compare the Objects only.
     * </p>
     */
    public BeanComparator() {
        this(null);
    }

    /**
     * <p>
     * Constructs a property-based comparator for beans. This compares two beans
     * by the property specified in the property parameter. This constructor
     * creates a <code>BeanComparator</code> that uses a
     * <code>ComparableComparator</code> to compare the property values.
     * </p>
     * <p/>
     * <p>
     * Passing "null" to this constructor will cause the BeanComparator to
     * compare objects based on natural order, that is
     * <code>java.lang.Comparable</code>.
     * </p>
     *
     * @param property String Name of a bean property, which may contain the name of
     *                 a simple, nested, indexed, mapped, or combined property. See
     *                 {@link PropertyUtilsBean} for property query language syntax.
     *                 If the property passed in is null then the actual objects will
     *                 be compared
     */
    public BeanComparator(String property) {
        this(property, ComparableComparator.getInstance());
    }

    /**
     * Constructs a property-based comparator for beans. This constructor
     * creates a BeanComparator that uses the supplied Comparator to compare the
     * property values.
     *
     * @param property   Name of a bean property, can contain the name of a simple,
     *                   nested, indexed, mapped, or combined property. See
     *                   {@link PropertyUtilsBean} for property query language syntax.
     * @param comparator BeanComparator will pass the values of the specified bean
     *                   property to this Comparator. If your bean property is not a
     *                   comparable or contains null values, a suitable comparator may
     *                   be supplied in this constructor.
     */
    public BeanComparator(String property, Comparator comparator) {
        setProperty(property);
        if (comparator != null) {
            this.comparator = comparator;
        } else {
            this.comparator = ComparableComparator.getInstance();
        }
    }

    /**
     * Sets the method to be called to compare two JavaBeans
     *
     * @param property String method name to call to compare If the property passed
     *                 in is null then the actual objects will be compared
     */
    public void setProperty(String property) {
        this.property = property;
    }

    /**
     * Gets the property attribute of the BeanComparator
     *
     * @return String method name to call to compare. A null value indicates
     * that the actual objects will be compared
     */
    public String getProperty() {
        return property;
    }

    /**
     * Gets the Comparator being used to compare beans.
     *
     * @return the Comparator being used to compare beans
     */
    public Comparator getComparator() {
        return comparator;
    }

    public static String firstCharToUpperCase(String str) {
        Character firstChar = str.charAt(0);
        String tail = str.substring(1);
        str = Character.toUpperCase(firstChar) + tail;
        return str;
    }

    /**
     * Compare two JavaBeans by their shared property. If {@link #getProperty}
     * is null then the actual objects will be compared.
     *
     * @param o1 Object The first bean to get data from to compare against
     * @param o2 Object The second bean to get data from to compare
     * @return int negative or positive based on order
     */
    public int compare(Object o1, Object o2) {

        if (property == null) {
            // compare the actual objects
            return comparator.compare(o1, o2);
        }

        MethodAccess access1 = MethodAccess.get(o1.getClass());
        MethodAccess access2 = MethodAccess.get(o2.getClass());

        Object value1 = access1.invoke(o1, "get" + firstCharToUpperCase(property));
        Object value2 = access2.invoke(o2, "get" + firstCharToUpperCase(property));
        if (value1 != null && value2 != null) {
            return comparator.compare(value1, value2);
        } else if (value1 != null && value2 == null) {
            return 1;
        } else if (value1 == null && value2 != null) {
            return -1;
        }
        return 1;
    }

    /**
     * Two <code>BeanComparator</code>'s are equals if and only if the wrapped
     * comparators and the property names to be compared are equal.
     *
     * @param o Comparator to compare to
     * @return whether the the comparators are equal or not
     */
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BeanComparator)) {
            return false;
        }

        final BeanComparator beanComparator = (BeanComparator) o;

        if (!comparator.equals(beanComparator.comparator)) {
            return false;
        }
        if (property != null) {
            if (!property.equals(beanComparator.property)) {
                return false;
            }
        } else {
            return (beanComparator.property == null);
        }

        return true;
    }

    /**
     * Hashcode compatible with equals.
     *
     * @return the hash code for this comparator
     */
    public int hashCode() {
        int result;
        result = comparator.hashCode();
        return result;
    }
}