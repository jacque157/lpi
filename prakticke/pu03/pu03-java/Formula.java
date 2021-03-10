import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;

class Constant
{
    String name;
    public Constant(String name) {
        this.name = name;
    }
    public String name() {
        return this.name;
    }
    public String eval(Structure m) {
        return m.iC(name());
    }
    @Override
    public String toString() {
        return name();
    }
    @Override
    public boolean equals(Object other)
    {
        if (this == other) return true;
        if (other == null) return false;
        if (getClass() != other.getClass()) return false;
        Constant otherC = (Constant) other;
        return name().equals(otherC.name());
    }
}

class Formula
{
    List<Formula> subfs = new ArrayList<Formula>();

    public List<Formula> subfs() {
        return subfs;
    }

    @Override
    public String toString()
    {
        throw new RuntimeException("Not implemented");
    }

    public boolean isTrue(Structure m) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public boolean equals(Object other) { throw new RuntimeException("Not implemented"); }

    public int deg() { throw new RuntimeException("Not implemented"); }

    public Set<AtomicFormula> atoms() {
        throw new RuntimeException("Not implemented");
    }

    public Set<String> constants() { throw new RuntimeException("Not implemented"); }

    public Set<String> predicates() {
        throw new RuntimeException("Not implemented");
    }
}

class AtomicFormula extends Formula
{
    AtomicFormula() { }

    @Override
    public int deg() { return 0;}
}

class PredicateAtom extends AtomicFormula
{
    String name;
    List<Constant> args = new ArrayList<>();

    PredicateAtom(String name, List<Constant> args)
    {
        this.name = name;
        this.args = args;
    }

    String name()
    {
        return name;
    }

    List<Constant> arguments() {
        return args;
    }

    @java.lang.Override
    public String toString()
    {
        StringBuffer repr = new StringBuffer(name + "(");
        String sep = "";
        for (Constant arg: args)
        {
            repr.append(sep);
            sep = ",";
            repr.append(arg.toString());
        }
        repr.append(")");
        return repr.toString();
    }

    @Override
    public boolean equals(Object other)
    {
        if (this == other) return true;
        if (other == null) return false;
        if (getClass() != other.getClass()) return false;
        PredicateAtom otherP = (PredicateAtom) other;

        if (! name().equals(otherP.name()))
            return false;

        for (Constant arg: arguments())
        {
            if ( ! otherP.arguments().contains(arg))
                return false;
        }

        for (Constant arg: otherP.arguments())
        {
            if ( ! arguments().contains(arg))
                return false;
        }

        return true;
    }

    public Set<String> constants()
    {
        Set<String> constants = new HashSet<>();
        for (Constant arg: arguments())
        {
            constants.add(arg.toString());
        }
        return constants;
    }

    public Set<String> predicates()
    {
        Set<String> predicate = new HashSet<>();
        predicate.add(name);
        return predicate;
    }

    public Set<AtomicFormula> atoms()
    {
        Set<AtomicFormula> atom = new HashSet<>();
        atom.add(this);
        return atom;
    }

    public boolean isTrue(Structure m)
    {
        List<String> iArgs = new ArrayList<>();
        for (int j = 0; j < args.size(); j++)
        {
            iArgs.add(m.iC(args.get(j).toString()));
        }

        return m.iP(name).contains(iArgs);
    }
}

class EqualityAtom extends AtomicFormula
{
    Constant left;
    Constant right;

    EqualityAtom(Constant left, Constant right)
    {
        this.left = left;
        this.right = right;
    }

    Constant left() { return left; }

    Constant right() {
        return right;
    }

    public String toString()
    {
        return left.toString() + "=" + right.toString();
    }

    @Override
    public boolean equals(Object other)
    {
        if (this == other) return true;
        if (other == null) return false;
        if (getClass() != other.getClass()) return false;
        EqualityAtom otherE = (EqualityAtom) other;


        if( ! otherE.left().equals(left()))
            return false;

        if( ! otherE.right().equals(right()))
            return false;

        return true;
    }

    public Set<String> constants()
    {
        Set<String> constants = new HashSet<>();

        constants.add(left.toString());
        constants.add(right.toString());

        return constants;
    }

    public Set<String> predicates()
    {
        Set<String> predicate = new HashSet<>();
        return predicate;
    }

    public Set<AtomicFormula> atoms()
    {
        Set<AtomicFormula> atom = new HashSet<>();
        atom.add(this);
        return atom;
    }

    public boolean isTrue(Structure m)
    {
        String i1 = m.iC(left.toString());
        String i2 = m.iC(right.toString());

        return i1.equals(i2);
    }
}

class Negation extends Formula
{
    Formula originalFormula;
    Negation(Formula originalFormula)
    {
        subfs.add(originalFormula);
        this.originalFormula = originalFormula;
    }

    public Formula originalFormula() {
        return originalFormula;
    }

    public String toString()
    {
        return "-" + originalFormula().toString();
    }

    @Override
    public boolean equals(Object other)
    {
        if (this == other) return true;
        if (other == null) return false;
        if (getClass() != other.getClass()) return false;
        Negation otherN = (Negation) other;

        return originalFormula().equals(otherN.originalFormula());
    }

    @Override
    public int deg() { return 1 + originalFormula().deg();}

    public Set<String> constants()
    {
        return originalFormula().constants();
    }

    public Set<String> predicates()
    {
        return originalFormula().predicates();
    }

    public Set<AtomicFormula> atoms()
    {
        return originalFormula().atoms();
    }

    public boolean isTrue(Structure m)
    {
        return ( ! originalFormula().isTrue(m));
    }
}

class Disjunction extends Formula
{
    List<Formula> disjuncts = new ArrayList<>();
    Disjunction(List<Formula> disjuncts)
    {
        subfs = disjuncts;
        this.disjuncts = disjuncts;
    }

    @java.lang.Override
    public String toString()
    {
        StringBuffer repr = new StringBuffer("(");
        String sep = "";
        for (Formula dis: disjuncts)
        {
            repr.append(sep);
            sep = "|";
            repr.append(dis.toString());
        }
        repr.append(")");
        return repr.toString();
    }

    @Override
    public boolean equals(Object other)
    {
        if (this == other) return true;
        if (other == null) return false;
        if (getClass() != other.getClass()) return false;
        Disjunction otherD = (Disjunction) other;

        return disjuncts.equals(otherD.disjuncts);
    }

    @Override
    public int deg()
    {
        int deg = 1;
        for (Formula dis: disjuncts)
        {
            deg += dis.deg();
        }
        return deg;
    }

    public Set<String> constants()
    {
        Set<String> constants = new HashSet<>();

        for (Formula dis: disjuncts)
        {
            for (String s: dis.constants())
            {
                constants.add(s);
            }
        }
        return constants;
    }

    public Set<String> predicates()
    {
        Set<String> predicates = new HashSet<>();

        for (Formula dis: disjuncts)
        {
            for (String s: dis.predicates())
            {
                predicates.add(s);
            }
        }
        return predicates;
    }

    public Set<AtomicFormula> atoms()
    {
        Set<AtomicFormula> atoms = new HashSet<>();

        for (Formula dis: disjuncts)
        {
            for (AtomicFormula a: dis.atoms())
            {
                atoms.add(a);
            }
        }
        return atoms;
    }

    public boolean isTrue(Structure m)
    {
        for (Formula dis: disjuncts)
        {
            if(dis.isTrue(m))
                return true;
        }
        return false;
    }
}

class Conjunction extends Formula
{
    List<Formula> conjuncts = new ArrayList<>();
    Conjunction(List<Formula> conjuncts)
    {
        subfs = conjuncts;
        this.conjuncts = conjuncts;
    }

    @java.lang.Override
    public String toString()
    {
        StringBuffer repr = new StringBuffer("(");
        String sep = "";
        for (Formula con: conjuncts)
        {
            repr.append(sep);
            sep = "&";
            repr.append(con.toString());
        }
        repr.append(")");
        return repr.toString();
    }

    @Override
    public boolean equals(Object other)
    {
        if (this == other) return true;
        if (other == null) return false;
        if (getClass() != other.getClass()) return false;
        Conjunction otherC = (Conjunction) other;

        return conjuncts.equals(otherC.conjuncts);
    }

    @Override
    public int deg()
    {
        int deg = 1;
        for (Formula con: conjuncts)
        {
            deg += con.deg();
        }
        return deg;
    }

    public Set<String> constants()
    {
        Set<String> constants = new HashSet<>();

        for (Formula con: conjuncts)
        {
            for (String s: con.constants())
            {
                constants.add(s);
            }
        }
        return constants;
    }

    public Set<String> predicates()
    {
        Set<String> predicates = new HashSet<>();

        for (Formula con: conjuncts)
        {
            for (String s: con.predicates())
            {
                predicates.add(s);
            }
        }
        return predicates;
    }

    public Set<AtomicFormula> atoms()
    {
        Set<AtomicFormula> atoms = new HashSet<>();

        for (Formula con: conjuncts)
        {
            for (AtomicFormula a: con.atoms())
            {
                atoms.add(a);
            }
        }
        return atoms;
    }

    public boolean isTrue(Structure m)
    {
        for (Formula con: conjuncts)
        {
            if ( ! con.isTrue(m))
                return false;
        }
        return true;
    }
}

class BinaryFormula extends Formula
{
    Formula left, right;
    BinaryFormula(Formula left, Formula right)
    {
        this.left = left;
        this.right= right;
    }

    public Formula leftSide()
    {
        return left;
    }

    public Formula rightSide()
    {
        return right;
    }

    public Set<String> constants()
    {
        Set<String> constants = new HashSet<>();
        for (String s: leftSide().constants())
        {
            constants.add(s);
        }

        for (String s: rightSide().constants())
        {
            constants.add(s);
        }
        return constants;
    }

    public Set<String> predicates()
    {
        Set<String> predicates = new HashSet<>();

        for (String s: leftSide().predicates())
        {
            predicates.add(s);
        }

        for (String s: rightSide().predicates())
        {
            predicates.add(s);
        }

        return predicates;
    }

    public Set<AtomicFormula> atoms()
    {
        Set<AtomicFormula> atoms = new HashSet<>();

        for (AtomicFormula a: leftSide().atoms())
        {
            atoms.add(a);
        }

        for (AtomicFormula a: rightSide().atoms())
        {
            atoms.add(a);
        }

        return atoms;
    }

    @Override
    public int deg() { return 1 + leftSide().deg() + rightSide().deg();}
}

class Implication extends BinaryFormula
{
    Implication(Formula left, Formula right)
    {
        super(left, right);
        subfs.add(left);
        subfs.add(right);
    }

    @java.lang.Override
    public String toString()
    {
        return "(" + left.toString() + "->" + right.toString() + ")";
    }

    @Override
    public boolean equals(Object other)
    {
        if (this == other) return true;
        if (other == null) return false;
        if (getClass() != other.getClass()) return false;
        Implication otherI = (Implication) other;

        if ( ! leftSide().equals(otherI.leftSide()))
            return false;

        if ( ! rightSide().equals(otherI.rightSide()))
            return false;

        return true;
    }

    public boolean isTrue(Structure m)
    {
        return (! leftSide().isTrue(m)) || rightSide().isTrue(m);
    }

}

class Equivalence extends BinaryFormula
{
    Equivalence(Formula left, Formula right)
    {
        super(left, right);
        subfs.add(left);
        subfs.add(right);
    }

    @java.lang.Override
    public String toString()
    {
        return "(" + left.toString() + "<->" + right.toString() + ")";
    }

    @Override
    public boolean equals(Object other)
    {
        if (this == other) return true;
        if (other == null) return false;
        if (getClass() != other.getClass()) return false;
        Equivalence otherE = (Equivalence) other;

        if ( ! leftSide().equals(otherE.leftSide()))
            return false;

        if ( ! rightSide().equals(otherE.rightSide()))
            return false;

        return true;
    }

    public boolean isTrue(Structure m)
    {
        return (leftSide().isTrue(m) && rightSide().isTrue(m)) || (( ! leftSide().isTrue(m)) && ( ! rightSide().isTrue(m)));
    }
}
