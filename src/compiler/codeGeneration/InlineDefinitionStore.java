package compiler.codeGeneration;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import util.StringUtils;
import util.exceptions.IndexOutOfBoundsException;
import annotations.JCHR_Inline;

import compiler.CHRIntermediateForm.variables.VariableFactory;

/**
 * In an inline definition each occurrence of:
 * <ul>
 * 	<li>
 * 		<code>%<i>0</i></code> is replaced with the implicit argument. 
 * 		For a static method this will be the class' name.
 * 	</li>
 * 	<li>
 * 		<code>%<i>i</i></code> with <code><i>i</i>&lt; 0</code> is
 * 		replaced with the <code><i>i</i></code>'th argument expression.
 * 		If <code><i>i</i></code> is larger than the number of arguments,
 * 		an {@link IndexOutOfBoundsException} is thrown.
 * 		If for some reason (though this is highly unlikely)
 * 		the <code>%<i>i</i></code> pattern needs to be followed 
 * 		by a number, use <code>$</code> to separate the pattern's 
 * 		number sequence from that of the following number, 
 * 		for instance: <code>%1$124</code>.
 * 		Note that this is the only occasion where a
 * 		<code>$</code> is allowed to end a pattern's number 
 * 		sequence. In all other cases it will be assumed to be
 * 		the start of a variable name (cf. next item).
 * 	</li>
 * 	<li>
 * 		<code>$<i>var</i>$</code> is replaced with a unique variable
 * 		name. Here <code><i>var</i></code> is any valid Java variable 
 * 		identifier (not containing a dollar character obviously).
 * 	</li>
 * 	<li>
 * 		<code>%%</code> is replaced with a <code>%</code> character.
 * 	</li>
 * 	<li>
 * 		<code>$$</code> is replaced with a <code>$</code> character.
 * 	</li>
 *  <li>
 *  	<code>%n</code> is replaced with a line break (system-dependent).
 *  </li>
 * </ul>
 */
public class InlineDefinitionStore {

	private final static Map<Method, String> INLINE_DEFINITIONS 
		= new HashMap<Method, String>();
	
	public static void putInlineDefinition(Method method, String value) {
		INLINE_DEFINITIONS.put(method, value);
	}
		
	static {
		try {
//			putInlineDefinition(
//				Math.class.getMethod("abs", double.class), 
//				"(%1$s <= 0.0D)? 0.0D - %1$s : %1$s"
//			);
//			putInlineDefinition(
//				Math.class.getMethod("abs", float.class), 
//				"(%1$s <= 0.0F)? 0.0F - %1$s : %1$s"
//			);
//			putInlineDefinition(
//				Math.class.getMethod("abs", int.class), 
//				"(%1$s < 0)? -%1$s : %1$s"
//			);
//			putInlineDefinition(
//				Math.class.getMethod("abs", long.class), 
//				"(%1$s < 0)? -%1$s : %1$s"
//			);
//			putInlineDefinition(
//				Math.class.getMethod("max", int.class, int.class), 
//				"((%1$s >= %2$s) ? %1$s : %2$s)"
//			);
//			putInlineDefinition(
//				Math.class.getMethod("max", long.class, long.class), 
//				"((%1$s >= %2$s) ? %1$s : %2$s)"
//			);
//			putInlineDefinition(
//				Math.class.getMethod("min", int.class, int.class), 
//				"((%1$s <= %2$s) ? %1$s : %2$s)"
//			);
//			putInlineDefinition(
//				Math.class.getMethod("min", long.class, long.class), 
//				"((%1$s <= %2$s) ? %1$s : %2$s)"
//			);
			putInlineDefinition(
				Math.class.getMethod("toDegrees", double.class), 
				"(%1 * 180.0 / Math.PI)"
			);
			putInlineDefinition(
				Math.class.getMethod("toRadians", double.class), 
				"(%1 / 180.0 * Math.PI)"
			);
		} catch (NoSuchMethodException e) {
			throw new InternalError();
		}
	}
	
	public static String getInlineDefinition(Method method) {
		String result = INLINE_DEFINITIONS.get(method);
		if (result != null) return result;
		if (method.isAnnotationPresent(JCHR_Inline.class))
			return method.getAnnotation(JCHR_Inline.class).value();
		return null;
	}
	
	public static String format(String format, String... args) throws IllegalFormatException {
		char[] in = format.toCharArray();
		StringBuilder out = new StringBuilder(2 * in.length);
		Map<String, String> map = new HashMap<String, String>(); 
		int i = 0;
		char c;
		normal: while (i < in.length) {
			switch (c = in[i++]) {
				case '%': 
					int k = 0; boolean b = false;
					percent: while (i < in.length) switch (c = in[i++]) {
						case '0': case '1': case '2': case '3': case '4':
						case '5': case '6': case '7': case '8': case '9':
							b = true;
							k = k * 10 + (c - '0');
						continue percent;
						
						case '$':
							if (!b) throw new IllegalFormatException(format, i);
							out.append(args[k]);
							
							if (i == in.length)
								throw new IllegalFormatException(format, i);
							if (in[i] < '0' || in[i] > '9')
								i--;	// causes the dollar char to be read again
						continue normal;
						
						case 'n':
							if (b)
								out.append(args[k]).append('n');
							else 
								out.append(StringUtils.getLineSeparator());
						continue normal;
						
						case '%':
							if (!b) {
								out.append('%');
								continue normal;
							} else {
								out.append(args[k]);
								k = 0; b = false;
								continue percent;
							}
							
						default:
							if (!b) throw new IllegalFormatException(format, i);
							out.append(args[k]).append(c);
						continue normal;	
					}
					if (!b) throw new IllegalFormatException(format, i);
					out.append(args[k]);
				break normal;
				
				case '$':
					int s = i;

					if (i == in.length)
						throw new IllegalFormatException(format, i);
					if ((c = in[i++]) == '$') {
						out.append('$');
						continue normal;
					} else {
						if (!Character.isJavaIdentifierStart(in[i]))
							throw new IllegalFormatException(format, i);
					}
					
					while (i < in.length) 
						if ((c = in[i++]) == '$') {
							String id = new String(in, s, i-s);
							String var = map.get(id);
							if (var == null) {
								var = VariableFactory.createImplicitVariableIdentifier();
								map.put(id, var);
							}
							out.append(var);
							continue normal;
						} else {
							if (!Character.isJavaIdentifierPart(c))
								throw new IllegalFormatException(format, i);
						}
					throw new IllegalFormatException(format, i);
					
				default:
					out.append(c);
			}
		}
		
		return out.toString();
	}
	
	public static class IllegalFormatException extends java.lang.IllegalArgumentException {
		private static final long serialVersionUID = 1L;
		
		private final int index;
		private final String format;
		
		public IllegalFormatException(String format, int index) {
			super(format + "@" + index);
			this.format = format;
			this.index = index;
		}
		
		public int getIndex() {
			return index;
		}
		public String getFormat() {
			return format;
		}
	}
}