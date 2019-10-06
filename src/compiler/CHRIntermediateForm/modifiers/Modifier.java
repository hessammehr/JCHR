package compiler.CHRIntermediateForm.modifiers;

import static java.lang.reflect.Modifier.PRIVATE;
import static java.lang.reflect.Modifier.PROTECTED;
import static java.lang.reflect.Modifier.PUBLIC;

public class Modifier {
    public final static int DEFAULT = 0;
    public final static int LOCAL = 0x40000000;   

    public static boolean isLocal(int modifiers) {
        return (modifiers & LOCAL) != 0;
    }
    public static boolean isDefaultAccess(int modifiers) {
        return getAccessModifier(modifiers) == 0;
    }
    
    public static int getAccessModifier(String accessModifier) {
        try { switch (accessModifier.charAt(2)) {
            case 'f':
                if (accessModifier.equals("default"))
                    return DEFAULT;
                else
                    break;
            case 'o':
                if (accessModifier.equals("protected"))
                    return PROTECTED;
                else
                    break;
            case 'b':
                if (accessModifier.equals("public"))
                    return PUBLIC;
                else
                    break;
            case 'i':
                if (accessModifier.equals("private"))
                    return PRIVATE;
                else
                    break;
            case 'c':
                if (accessModifier.equals("local"))
                    return LOCAL;
                else
                    break;
            }
        } catch (NullPointerException npe) {
            // illegal argument
        } catch (IndexOutOfBoundsException iobe) {
            // illegal argument
        }
        
        throw new IllegalArgumentException(accessModifier);
    }
    
    public static int getAccessModifier(int modifiers) {
        return modifiers & (PRIVATE | PUBLIC | PROTECTED | LOCAL);
    }
    public static int getAccessModifierFor(IModified modified) {
        return modified.getModifiers() & (PRIVATE | PUBLIC | PROTECTED | LOCAL);
    }
    public static String getAccessStringFor(IModified modified) {
        return toAccessString(modified.getModifiers());
    }
    public static String toAccessString(int modifiers) {
        if (java.lang.reflect.Modifier.isPublic(modifiers))    return "public";
        if (java.lang.reflect.Modifier.isProtected(modifiers)) return "protected";
        if (java.lang.reflect.Modifier.isPrivate(modifiers))   return "private";
        return "";
    }
    
    public static boolean isPrivate(IModified modified) {
    	return java.lang.reflect.Modifier.isPrivate(modified.getModifiers());
    }
    public static boolean isPublic(IModified modified) {
    	return java.lang.reflect.Modifier.isPublic(modified.getModifiers());
    }
    public static boolean isProtected(IModified modified) {
    	return java.lang.reflect.Modifier.isProtected(modified.getModifiers());
    }
    public static boolean isLocal(IModified modified) {
    	return getAccessModifierFor(modified) == LOCAL;
    }
    public static boolean isDefaultAccess(IModified modified) {
    	return getAccessModifierFor(modified) == DEFAULT;
    }
    
    public static boolean isExported(IModified modified) {
    	return !isPrivate(modified) && !isLocal(modified);
    }
}
