package compiler.CHRIntermediateForm.builder.tables;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import compiler.CHRIntermediateForm.exceptions.DuplicateIdentifierException;
import compiler.CHRIntermediateForm.exceptions.IllegalIdentifierException;
import compiler.CHRIntermediateForm.id.Identifier;
import compiler.CHRIntermediateForm.rulez.Rule;
import compiler.CHRIntermediateForm.rulez.RuleType;

/**
 * @author Peter Van Weert
 */
public class RuleTable extends SymbolTable<Rule> {
    public Rule declareRule(String id, RuleType type)
            throws IllegalIdentifierException, DuplicateIdentifierException {
        
        if (id == null) 
            id = createID();        
        else 
            Identifier.testUdSimpleIdentifier(id);
        
        return declare(id, Rule.newInstance(id, getNextNbr(), type));
    }

    @Override
    public String createID() {
        return createID("rule");
    }

    /**
     * Geeft een lijst terug van de regels, gesorteerd op nummer. Dit is nodig,
     * want de onderliggende HashMap heeft geen enkele garantie op de volgorde
     * waarin de regels worden bijgehouden. Dit resulteerde in fouten waar
     * <code>Rule.getRuleNbr()</code> niet overeenstemde met de volgorde in
     * deze collectie!
     * 
     * @see compiler.CHRIntermediateForm.builder.tables.SymbolTable#getValues()
     * @see Rule.getRuleNbr()
     */
    @Override
    public List<Rule> getValues() {
        ArrayList<Rule> result = new ArrayList<Rule>(super.getValues());
        Collections.sort(result);
        return result;
    }
}