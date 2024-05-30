package tech.wetech.api.common.authz;

import tech.wetech.api.common.CollectionUtils;
import tech.wetech.api.common.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
判断一个User是否具备调用某个handler方法的资格：使用isPermitted比较User对应UserDTO对象的permissions集合属性和对应方法RequirePermission注解中的
permissions字符串数组。首先在isPermitted方法中会根据逻辑需求进行两种判断：与->必须保证RequirePermission注解中permissions字符串数组的每个字符串
经setParts方法生成的permission对象都’存在于‘UserDTO对象的permissions集合中；或->只需保证RequirePermission注解中permissions字符串数组的每
个字符串经setParts方法生成的permission对象中存在一个在UserDTO对象的permissions集合中也‘存在’的个体即可。
上述标注了单引号的“permissions（permission）存在（存在于）permission（permissions）“是指hasPermission(permissions,permission)返回true，
这并不是简单的判断某个集合中是否存在某特定元素，要让hasPermission(permissions,permission)返回true则需要保证在permissions中存在某个permission
对象p1，使得p1.implies(permission)和(permission).implies(p1)均返回true。implies方法用于判断两个permission对象p1，p2的part(List<Set<String>>)
属性之间的关系，若p1.implies(p2)返回true，则说明：当p1.parts.size<=p2.parts.size时，则p1.parts中的每个Set<String>集合都完全包含p2.parts中相同
位置上对应的Set<String>集合；当p1.parts.size>p2.parts.size时，则除p1.parts中的每个Set<String>集合都完全包含p2.parts中相同位置上对应的
Set<String>集合外，p1.parts中多于p2.parts的那部分List<Set<String>>中的每一个Set<String>都必须包含有通配符WILDCARD_TOKEN。
 * @author Jansonci
 */
public class PermissionHelper {
  public static boolean isPermitted(Set<String> permissions, String[] value, Logical logical) {
    if (logical == Logical.AND) {
      boolean allMatch = true;
      for (String permission : value) {
        if (!hasPermission(permissions, permission)) {
          allMatch = false;
          break;
        }
      }
      return allMatch;
    } else if (logical == Logical.OR) {
      boolean anyMatch = false;
      for (String permission : value) {
        if (hasPermission(permissions, permission)) {
          anyMatch = true;
          break;
        }
      }
      return anyMatch;
    }
    return false;
  }

  /**
   * 判断是否有对应权限
   *
   * @param permissions
   * @param permission
   * @return
   */
  public static boolean hasPermission(Set<String> permissions, String permission) {
    if (StringUtils.isEmpty(permission)) {
      return true;
    }
    for (String p : permissions) {
      Permission p1 = new Permission(p);
      Permission p2 = new Permission(permission);
      if (p1.implies(p2) || p2.implies(p1)) {
        return true;
      }
    }
    return false;
  }

  protected static class Permission {

    protected static final String WILDCARD_TOKEN = "*";
    protected static final String PART_DIVIDER_TOKEN = ":";
    protected static final String SUBPART_DIVIDER_TOKEN = ",";
    protected static final boolean DEFAULT_CASE_SENSITIVE = false;

    private List<Set<String>> parts;

    protected Permission() {
    }

    public Permission(String wildcardString) {
      this(wildcardString, DEFAULT_CASE_SENSITIVE);
    }

    public Permission(String wildcardString, boolean caseSensitive) {
      setParts(wildcardString, caseSensitive);
    }

    protected void setParts(String wildcardString) {
      setParts(wildcardString, DEFAULT_CASE_SENSITIVE);
    }

    protected void setParts(String wildcardString, boolean caseSensitive) {
      if (wildcardString == null || wildcardString.trim().length() == 0) {
        throw new IllegalArgumentException("Wildcard string cannot be null or empty. Make sure permission strings are properly formatted.");
      }

      wildcardString = wildcardString.trim();

      List<String> parts = CollectionUtils.asList(wildcardString.split(PART_DIVIDER_TOKEN));

      this.parts = new ArrayList<Set<String>>();
      for (String part : parts) {
        Set<String> subparts = CollectionUtils.asSet(part.split(SUBPART_DIVIDER_TOKEN));
        if (!caseSensitive) {
          subparts = lowercase(subparts);
        }
        if (subparts.isEmpty()) {
          throw new IllegalArgumentException("Wildcard string cannot contain parts with only dividers. Make sure permission strings are properly formatted.");
        }
        this.parts.add(subparts);
      }

      if (this.parts.isEmpty()) {
        throw new IllegalArgumentException("Wildcard string cannot contain only dividers. Make sure permission strings are properly formatted.");
      }
    }

    private Set<String> lowercase(Set<String> subparts) {
      Set<String> lowerCasedSubparts = new LinkedHashSet<String>(subparts.size());
      for (String subpart : subparts) {
        lowerCasedSubparts.add(subpart.toLowerCase());
      }
      return lowerCasedSubparts;
    }

    protected List<Set<String>> getParts() {
      return this.parts;
    }


    public boolean implies(Permission p) {
      // By default, only supports comparisons with other WildcardPermissions
      if (!(p instanceof Permission)) {
        return false;
      }
      Permission wp = (Permission) p;

      List<Set<String>> otherParts = wp.getParts();

      int i = 0;
      for (Set<String> otherPart : otherParts) {
        // If this permission has fewer parts than the other permission, everything after the number of parts contained
        // in this permission is automatically implied, so return true
        if (getParts().size() - 1 < i) {
          return true;
        } else {
          Set<String> part = getParts().get(i);
          if (!part.contains(WILDCARD_TOKEN) && !part.containsAll(otherPart)) {
            return false;
          }
          i++;
        }
      }
      // If this permission has more parts than the other parts, only imply it if all the other parts are wildcards
      for (; i < getParts().size(); i++) {
        Set<String> part = getParts().get(i);
        if (!part.contains(WILDCARD_TOKEN)) {
          return false;
        }
      }
      return true;
    }
  }

}
