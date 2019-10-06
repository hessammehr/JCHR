package compiler.CHRIntermediateForm.builder.tables;

import compiler.CHRIntermediateForm.constraints.ud.Occurrence;
import compiler.CHRIntermediateForm.exceptions.DuplicateIdentifierException;
import compiler.CHRIntermediateForm.exceptions.IllegalIdentifierException;
import compiler.CHRIntermediateForm.id.Identifier;

public class OccurrenceTable extends SymbolTable<Occurrence>{
    
    public void identify(String identifier, Occurrence occurrence)
        throws DuplicateIdentifierException, IllegalIdentifierException {

        Identifier.testUdSimpleIdentifier(identifier, true);
        if (getValues().contains(occurrence))
            throw new DuplicateIdentifierException(
                "The occurrence " + occurrence + " has already received an identifier!"
            );
        declare(identifier, occurrence);
    }
}
