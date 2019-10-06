package compiler.CHRIntermediateForm.arg.arguments;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import compiler.CHRIntermediateForm.arg.argument.IArgument;
import compiler.CHRIntermediateForm.arg.argument.IImplicitArgument;
import compiler.CHRIntermediateForm.matching.MatchingInfos;

public abstract class ArgumentsDecorator implements IArguments {
    private IArguments arguments;
    
    public IArguments getArguments() {
        return arguments;
    }
    protected void setArguments(IArguments arguments) {
        this.arguments = arguments;
    }
    
    public List<IArgument> asList() {
        return getArguments().asList();
    }
    
    public boolean isMutable() {
        return getArguments().isMutable();
    }
    
    public Iterator<IArgument> iterator() {
        return getArguments().iterator();
    }
    public ListIterator<IArgument> listIterator() {
        return getArguments().listIterator();
    }
    public ListIterator<IArgument> listIterator(int index) throws IndexOutOfBoundsException {
        return getArguments().listIterator();
    }

    public int getArity() {
        return getArguments().getArity();
    }

    public IArgument getArgumentAt(int index) {
        return getArguments().getArgumentAt(index);
    }
    
    public int getIndexOf(IArgument argument) {
        return getArguments().getIndexOf(argument);
    }
    
    public compiler.CHRIntermediateForm.types.IType[] getTypes() {
        return getArguments().getTypes();
    }
    
    public compiler.CHRIntermediateForm.types.IType getTypeAt(int index) {
        return getArguments().getTypeAt(index);
    }

    public void addArgument(IArgument argument) {
        getArguments().addArgument(argument);
    }
    
    public void replaceArgumentAt(int index,IArgument argument) {
        getArguments().replaceArgumentAt(index, argument);
    }
    
    public void addArgumentAt(int index,IArgument argument) {
        getArguments().addArgumentAt(index, argument);
    }

    public void addImplicitArgument(IImplicitArgument implicitArgument) {
        getArguments().addImplicitArgument(implicitArgument);
    }
    
    public boolean hasImplicitArgument() {
        return getArguments().hasImplicitArgument();
    }
    
    public void markFirstAsImplicitArgument() {
        getArguments().markFirstAsImplicitArgument();
    }
    
    public void removeImplicitArgument() {
        getArguments().removeImplicitArgument();
    }
    
    public void removeImplicitArgumentMark() {
        getArguments().removeImplicitArgumentMark();
    }
    
    public void incorporate(MatchingInfos assignmentInfos, boolean ignoreImplicitArgument) {
        getArguments().incorporate(assignmentInfos, ignoreImplicitArgument);            
    }
    
    public boolean allFixed() {
        return getArguments().allFixed();
    }
    
    public boolean allConstant() {
        return getArguments().allConstant();
    }
    
    @Override
    public String toString() {
        return getArguments().toString();
    }
}