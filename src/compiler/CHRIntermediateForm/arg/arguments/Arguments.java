package compiler.CHRIntermediateForm.arg.arguments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import util.collections.Singleton;
import util.exceptions.IllegalStateException;

import compiler.CHRIntermediateForm.arg.argument.IArgument;
import compiler.CHRIntermediateForm.arg.argument.IImplicitArgument;
import compiler.CHRIntermediateForm.matching.CoerceMethod;
import compiler.CHRIntermediateForm.matching.MatchingInfo;
import compiler.CHRIntermediateForm.matching.MatchingInfos;
import compiler.CHRIntermediateForm.types.IType;

/**
 * @author Peter Van Weert
 */
public class Arguments implements IArguments {
    
    private List<IArgument> arguments;
       
    private boolean hasImplicitArgument;
    
    public Arguments() {
        this(new ArrayList<IArgument>());
    }
    
    public Arguments(int arity) {
        this(new ArrayList<IArgument>(arity));
    }
    
    public Arguments(IArgument... argument) {
        this(new ArrayList<IArgument>(Arrays.asList(argument)));
    }
    
    public Arguments(IArguments arguments) {
        this(new ArrayList<IArgument>(arguments.asList()));
        if (arguments.hasImplicitArgument()) markFirstAsImplicitArgument();
    }
    
    public Arguments(List<IArgument> arguments) {
        setArgumentList(arguments);
    }
    
    public Arguments(IImplicitArgument implicitArgument) {
        setArgumentList(new Singleton<IArgument>(implicitArgument));
        markFirstAsImplicitArgument();
    }
    
    public boolean isMutable() {
        return true;
    }
    
    public List<IArgument> asList() {
        return arguments;
    }
    public Iterator<IArgument> iterator() {
        return arguments.iterator();
    }
    public ListIterator<IArgument> listIterator() {
        return arguments.listIterator();
    }
    public ListIterator<IArgument> listIterator(int index) throws IndexOutOfBoundsException {
        return arguments.listIterator(index);
    }
    public int getArity() {
        return asList().size();
    }
    public IArgument getArgumentAt(int index) {
        return asList().get(index);
    }
    public int getIndexOf(IArgument argument) {
        return asList().indexOf(argument);
    }
    public void replaceArgumentAt(int index,IArgument arguement) {
        setArgumentAt(index, arguement);
    }
    public void addArgumentAt(int index,IArgument argument) {
        asList().add(index, argument);
    }
    protected void setArgumentAt(int index, IArgument argument) {
        asList().set(index, argument);
    }
    public IType getTypeAt(int index) {
        return getArgumentAt(index).getType();
    }
    protected void setArgumentList(List<IArgument> arguments) {
        this.arguments = arguments;
    }
    
    public void addArgument(IArgument argument) {
        asList().add(argument);
    }
    
    public IType[] getTypes() {
        IType[] result = new IType[getArity()];
        for (int i = 0; i < getArity(); i++)
            result[i] = getTypeAt(i);
        return result;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder().append('(');
        
        final int nbArgs = getArity();
        int i = hasImplicitArgument()? 1 : 0;
        if (nbArgs > i)
            builder.append(getArgumentAt(i));
        while (++i < nbArgs)
            builder.append(", ").append(getArgumentAt(i));
        
        return builder.append(')').toString();
    }
    
    public void addImplicitArgument(IImplicitArgument implicitArgument) {
        if (hasImplicitArgument()) {
//            removeImplicitArgument();}{
            if (! getArgumentAt(0).equals(implicitArgument))
                throw new IllegalStateException("Illegal implicit argument (%s != %s)", getArgumentAt(0), implicitArgument);
        } else {
            asList().add(0, implicitArgument);
            markFirstAsImplicitArgument();
        }
    }
    
    public void removeImplicitArgument() {
        asList().remove(0);
        removeImplicitArgumentMark();
    }
    
    public boolean hasImplicitArgument() {        
        return hasImplicitArgument;
    }
    
    public void markFirstAsImplicitArgument() {
        hasImplicitArgument = true;
    }
    
    public void removeImplicitArgumentMark() {
        hasImplicitArgument = false;
    }
    
    public void incorporate(MatchingInfos assignmentInfos, boolean ignoreImplicitArgument) {
        if (assignmentInfos.isDirectMatch()) return;

        IArgument argument;
        MatchingInfo info;        
        final int arity = getArity();
        for (int i = ignoreImplicitArgument? 1 : 0; i < arity; i++) {
            argument = getArgumentAt(i);
            info = assignmentInfos.getAssignmentInfoAt(i);
            for (CoerceMethod method : info.getCoerceMethods())
                argument = method.getInstance(argument);
            if (info.isInitMatch())
                argument = info.getInitialisator().getInstance(argument);
            setArgumentAt(i, argument);
        }
    }
    
    public boolean allFixed() {
        for (IArgument argument : this)
            if (! argument.isFixed()) return false;
        return true;
    }
    
    public boolean allConstant() {
        for (IArgument argument : this)
            if (! argument.isConstant()) return false;
        return true;
    }
    
    @Override
    public boolean equals(Object obj) {
        return (obj instanceof IArguments)
            && this.equals((IArguments)obj);
    }
    
    @Override
    public int hashCode() {
        if (getArity() == 0) return 0;
        return asList().hashCode();
    }
    
    public boolean equals(IArguments other) {
        if (other == null) return false;
        if (this == other) return true;
        if (this.getArity() != other.getArity()) return false;
        
        Iterator<IArgument> iter1 = this.iterator();
        Iterator<IArgument> iter2 = other.iterator();
        
        while (iter1.hasNext())
            if (! iter1.next().equals(iter2.next()))
                return false;
        
        return true;
    }
}