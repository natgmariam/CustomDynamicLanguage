import javax.xml.namespace.QName;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class Main {
    // this global variable indicates if an error has occurred.
    public static boolean error = false;
    // this global symbol table represents the scope of variables.
    public static SymbolTable table = new SymbolTable();
    // this global variable indicates if a variable/value is returned
    // by a lambda function.
    public static Stack<Value> returnVariables = new Stack<>();

    // ===-----------------------------------------------------------------===
    // the AST clas is the super-class for abstract syntax trees.
    // Every AST node hast its own subclass.
    public static abstract class AST {
        protected String label = "EMPTY";
        protected LinkedList<AST> children = new LinkedList<>();

        public AST() { }

        public String getLabel() {
            return label;
        }

        public List<AST> getChildren() {
            return Collections.unmodifiableList(children);
        }

        public abstract void addChild(AST child);
    }

    // ===-----------------------------------------------------------------===
    // every AST node that is a not a Stmt is an Expr. These
    // represent actual computations that return something, such
    // as a Value object.
    public static class Expr extends AST {
        protected Value eval() {
            if (!error) {
                System.err.println("eval() not yet implemented for " + this.getClass().getSimpleName());
                error = true;
            }
            return new Value();
        }

        @Override
        public void addChild(AST child) {
            children.add(child);
        }
    }

    // ===-----------------------------------------------------------------===
    // an identifier such as the name of a variable or function.
    public static class Identifier extends Expr {
        protected String value;

        public Identifier(final String value) {
            this.value = value;
            this.label = "Identifier `" + value + "`";
        }

        public void setValue(final String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        protected Value eval() {
            // TODO: students need to complete this   //done
            if (!error) {
                // CODE HERE
                // return value from table
                return table.lookup(value);

            }
            return new Value();
        }
    }

    // ===-----------------------------------------------------------------===
    // a literal number in the program.
    public static class Number extends Expr {
        protected int value;

        public Number(final int value) {
            this.value = value;
            this.label = "Number `" + value + "`";
        }

        @Override
        protected Value eval() {
            return new Value(value);
        }
    }

    // ===-----------------------------------------------------------------===
    // A literal boolean value such as "true" or "false".
    public static class BoolExpr extends Expr {
        private final boolean value;

        public BoolExpr(final boolean value) {
            this.value = value;
            this.label = "Boolean `" + value + "`";
        }

        @Override
        protected Value eval() {
            // TODO: students need to complete this  //done
            // CODE HERE
            //return val
            return new Value(value);
        }
    }

    // ===-----------------------------------------------------------------===
    // this gives the type to the different kinds of operators.
    public enum Oper {
        ADD, SUB, MUL, DIV, LT, GT, LE, GE, EQ, NE, AND, OR, NOT
    }

    // a binary operation for arithmetic operations such as +, -, *, or /.
    public static class ArithmeticOp extends Expr {
        private final Oper op;
        private final Expr left;
        private final Expr right;

        public ArithmeticOp(Expr left, Expr right, Oper op) {
            this.op = op;
            this.left = left;
            this.right = right;
            this.label = "ArithmeticOp `<left> <op> <right>`";
            addChild(left);
            addChild(right);
        }

        @Override
        protected Value eval() {
            final int l = left.eval().getNumber();
            final int r = right.eval().getNumber();
            switch (op) {
                case ADD:
                    return new Value(l + r);
                case SUB:
                    return new Value(l - r);
                case MUL:
                    return new Value(l * r);
                case DIV: {
                    if (r != 0) return new Value(l / r);
                    else if (!error) {
                        System.err.println("ERROR: Division by zero!");
                        error = true;
                    }
                }
            }
            return new Value();
        }
    }

    // ===-----------------------------------------------------------------===
    // a binary operator for comparison such as < or !=.
    public static class ComparisonOp extends Expr {
        private final Oper op;
        private final Expr left;
        private final Expr right;

        public ComparisonOp(Expr left, Expr right, Oper op) {
            this.left = left;
            this.right = right;
            this.op = op;
            this.label = "ComparisonOp `<left> <op> <right>`";
            addChild(left);
            addChild(right);
        }

        @Override
        protected Value eval() {
            // TODO: students need to complete this //done
            final int l = left.eval().getNumber();
            final int r = right.eval().getNumber();
            // CODE HERE
//          cmpr statments

            // returns the results
            //java style switch statment
            return switch (op) {
                case LT -> new Value(l < r);
                case GT -> new Value(l > r);
                case LE -> new Value(l <= r);
                case GE -> new Value(l >= r);
                case EQ -> new Value(l == r);
                case NE -> new Value(l != r);
                default -> new Value();
            };
        }
    }

    // ===-----------------------------------------------------------------===
    // a binary operation for boolean logic such as and, or.
    public static class BooleanOp extends Expr {
        private final Oper op;
        private final Expr left;
        private final Expr right;

        public BooleanOp(Expr left, Expr right, Oper op) {
            this.left = left;
            this.right = right;
            this.op = op;
            this.label = "BooleanOp `<left> <op> <right>`";
            addChild(left);
            addChild(right);
        }

        @Override
        protected Value eval() {
            // TODO: students need to complete this  //done
            // returns the results of our comparison statement
            //java style switch return
            final boolean l = left.eval().getBoolean();
            final boolean r = right.eval().getBoolean();
            // CODE HERE
            return switch (op) {
                case AND -> new Value(l && r);
                case OR -> new Value(l || r);
                default ->

                        new Value();
            };
        }
    }

    // ===-----------------------------------------------------------------===
    // this class represents a unary negation operation.
    public static class NegationOp extends Expr {
        private final Expr right;

        public NegationOp(Expr right) {
            this.right = right;
            this.label = "NegationOp `<op> <right>`";
            addChild(right);
        }

        @Override
        protected Value eval() {
            // TODO: students need to complete this  //done
            // return the negative value of our number
            final int r = right.eval().getNumber();
            return new Value(-1 * r);
        }
    }

    // ===-----------------------------------------------------------------===
    // this class represents a unary not operator.
    public static class NotOp extends Expr {
        private final Expr right;

        public NotOp(Expr right) {
            this.right = right;
            this.label = "NotOp `<op> <right>`";
            addChild(right);
        }

        @Override
        protected Value eval() {
            // TODO: students need to complete this  //done
            final boolean r = right.eval().getBoolean();
            return new Value(!r);
        }
    }

    // ===-----------------------------------------------------------------===
    // a read expression.
    public static class Read extends Expr {
        public Read() {
            this.label = "Read";
        }

        @Override
        protected Value eval() {
            System.out.print("read> ");
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            try {
                final String val = reader.readLine();
                return new Value(Integer.parseInt(val));
            } catch (Exception ignored) {
            }
            return new Value();
        }
    }

    // ===-----------------------------------------------------------------===
    // a return expression.
    public static class ReturnExpr extends Expr {
        private final Expr value;

        public ReturnExpr(Expr value) {
            this.value = value;
            this.label = "ReturnExpr `ret := <expr>`";
            addChild(value);
        }

        @Override
        protected Value eval() {
            // TODO: students need to complete this  //done
            if (!error) {
//                 CODE HERE
                //eval the value expr and return result
                //idk why it doesnt work
                Value result = value.eval();
                returnVariables.push(result);
                return new Value();
            }
            //default return if an error
            return new Value();
        }
    }

    // ===-----------------------------------------------------------------===
    // a Stmt is anything that can be evaluated at the top level such
    // as I/O, assignments, and control structures. Note that the last
    // child of any statement is the next statement in sequence.
    public static class Stmt extends AST {
        private Stmt next;

        public Stmt() {
            next = new NullStmt();
            children.add(next);
        }

        public Stmt(Stmt next) {
            if (next != null) children.add(next);
            this.next = next;
        }

//         this method is for building sequences of statements by the
//         parser. It takes two statements and appends one at the end
//         of the other. The returned value is a reference to the new
//         statement representing the sequence.
        public static Stmt append(Stmt a, Stmt b) {
            if (!a.hasNext()) return b;
            Stmt last = a;
            while (last.getNext().hasNext())
                last = last.getNext();
            last.setNext(b);
            return a;
        }

        public Stmt getNext() {
            return next;
        }

        public void setNext(Stmt next) {
            children.removeLast();
            children.add(next);
            this.next = next;
        }

        public boolean hasNext() {
            return next != null;
        }

        public void exec() {
            if (!error) {
                System.err.println("exec() not yet implemented for " + this.getClass().getSimpleName());
                error = true;
            }
        }

        @Override
        public void addChild(AST child) {
            // this inserts before the last thing in the list
            children.add(children.size() - 1, child);
        }
    }

    // ===-----------------------------------------------------------------===
    // this class is necessary to terminate a sequence of statements.
    public static class NullStmt extends Stmt {
        public NullStmt() {
            super(null);
            this.label = "NullStmt `null`";
        }

        // nothing to execute!
        @Override
        public void exec() { }
    }

    // ===-----------------------------------------------------------------===
    // this class is a statement for a block of code; i.e., code enclosed
    // in curly braces { and }. This is where scopes will begin and end.
    public static class Block extends Stmt {
        private Stmt body;

        public Block(Stmt body) {
            this.body = body;
            this.label = "Block `{ <stmt> }`";
            addChild(body);
        }

        public void setBody(Stmt body) {
            this.body = body;
        }

        public Stmt getBody() {
            return body;
        }

        @Override
        public void exec() {
            // TODO: students need to complete this   //might be done
            // CODE HERE
           //open scope
            //body gets called so exec
            //then close scope
            table.openScope();
            body.exec();
            table.closeScope();

        }
    }

    // ===-----------------------------------------------------------------===
    // this is a class for "if" and "if-else" statements.
    public static class IfStmt extends Stmt {
        private Expr condition;
        private Stmt ifBlock;
        private Stmt elseBlock;

        public IfStmt(Expr condition, Stmt ifBlock, Stmt elseBlock) {
            this.condition = condition;
            this.ifBlock = ifBlock;
            this.elseBlock = elseBlock;
            this.label = "IfStmt `if <expr> { <stmt> }`";
            addChild(condition);
            if (ifBlock != null)
                addChild(ifBlock);
            if (elseBlock != null)
                addChild(elseBlock);
            else
                addChild(new NullStmt());
        }

        public void setCondition(Expr condition) {
            this.condition = condition;
        }

        public void setIfBlock(Stmt ifBlock) {
            this.ifBlock = ifBlock;
        }

        public void setElseBlock(Stmt elseBlock) {
            this.elseBlock = elseBlock;
        }

        public Expr getCondition() {
            return condition;
        }

        public Stmt getIfBlock() {
            return ifBlock;
        }

        public Stmt getElseBlock() {
            return elseBlock;
        }

        @Override
        public void exec() {
            // TODO: students need to complete this  // done
            if (!error) {
                // CODE HERE
                //if value from map return true / use getBoolean call
                //then execute if block
                //else if the else block has value, execute else block
                //if else block is null only if condition will happen

                boolean check = condition.eval().getBoolean();

                if(check && ifBlock != null)
                {
                    ifBlock.exec();
                }
                else if(!check && elseBlock != null)
                {
                    elseBlock.exec();
                }

            }
            getNext().exec();
        }
    }

    // ===-----------------------------------------------------------------===
    // this class is for "while" statements.
    public static class WhileStmt extends Stmt {
        private Expr condition;
        private Stmt body;

        public WhileStmt(Expr condition, Stmt body) {
            this.condition = condition;
            this.body = body;
            this.label = "WhileStmt `while <expr> { <stmt> }`";
            addChild(condition);
            if (body != null)
                addChild(body);
        }

        public void setCondition(Expr condition) {
            this.condition = condition;
        }

        public void setBody(Stmt body) {
            this.body = body;
        }

        public Expr getCondition() {
            return condition;
        }

        public Stmt getBody() {
            return body;
        }

        @Override
        public void exec() {
            // TODO: students need to complete this   //not done
            if (!error) {
                // CODE HERE
                //while condiotn return true
                //execute body and keep on checking
                //last check to stop infinite loop
                boolean loopCheck = condition.eval().getBoolean();
                while(loopCheck) {
                    body.exec();
                    loopCheck = condition.eval().getBoolean();
                }
            }
            getNext().exec();
        }
    }

    // ===-----------------------------------------------------------------===
    // this is a "new" statement creates a new binding of the variable
    // to the stated value
    public static class AutoStmt extends Stmt {
        private Identifier lhs;
        private Expr rhs;

        public AutoStmt(Identifier lhs, Expr rhs) {
            this.lhs = lhs;
            this.rhs = rhs;
            this.label = "AutoStmt `new <var> := <expr>`";
            addChild(lhs);
            addChild(rhs);
        }

        public void setLhs(Identifier lhs) {
            this.lhs = lhs;
        }

        public void setRhs(Expr rhs) {
            this.rhs = rhs;
        }

        public Identifier getLhs() {
            return lhs;
        }

        public Expr getRhs() {
            return rhs;
        }

        @Override
        public void exec() {
            table.bind(lhs.getValue(), rhs.eval());
            getNext().exec();
        }
    }

    // ===-----------------------------------------------------------------===
    // this is an assignment statement. This represents RE-binding in
    // the symbol table.
    public static class AssignStmt extends Stmt {
        private Identifier lhs;
        private Expr rhs;

        public AssignStmt(Identifier lhs, Expr rhs) {
            this.lhs = lhs;
            this.rhs = rhs;
            this.label = " AssignStmt`<var> := <expr> `";
            addChild(lhs);
            addChild(rhs);
        }

        public void setLhs(Identifier lhs) {
            this.lhs = lhs;
        }

        public void setRhs(Expr rhs) {
            this.rhs = rhs;
        }

        public Identifier getLhs() {
            return lhs;
        }

        public Expr getRhs() {
            return rhs;
        }

        @Override
        public void exec() {
            // TODO: complete by implementation   //done
            if (!error) {
                // CODE HERE
                // rebind variable (follow syntax)
                //same syntax as AutoStmt
                System.out.println("INFO: replacing the value `" + rhs.eval() + "` in `" + lhs.getValue() + "`");
                table.rebind(lhs.getValue(), rhs.eval());
            }
            getNext().exec();
        }
    }

    // ===-----------------------------------------------------------------===
    // this is a write statement.
    public static class Write extends Stmt {
        private final Expr value;

        public Write(Expr value) {
            this.value = value;
            this.label = "Write `write <value>`";
            addChild(value);
        }

        @Override
        public void exec() {
            Value val = value.eval();
            if (!error)
                val.writeTo();
            getNext().exec();
        }
    }

    // ===-----------------------------------------------------------------===
    // an expression statement that consists of a single expression.
    public static class ExprStmt extends Stmt {
        private final Expr value;

        public ExprStmt(Expr value) {
            this.value = value;
            this.label = "ExprStmt `<expr>`";
            addChild(value);
        }

        @Override
        public void exec() {
            if (!error)
                value.eval();
            getNext().exec();
        }
    }

    // ===-----------------------------------------------------------------===
    // a lambda expression consists of a parameter name and a body.
    public static class Lambda extends Expr {
        private final Identifier variable;
        private final Stmt body;

        public Lambda(Identifier variable, Stmt body) {
            this.variable = variable;
            this.body = body;
            this.label = "Lambda `lambda <var> { <stmt> }`";
            addChild(variable);
            addChild(body);
        }

        public String getVariable() {
            return variable.getValue();
        }

        public Stmt getBody() {
            //System.out.println(body);
            return body;
        }

        @Override
        protected Value eval() {
            // TODO: students must complete this     //done
            if (!error) {
                // CODE HERE
                // call newLambda val
                Lambda newLambda = new Lambda(variable, body);

                return new Value(newLambda);
            }
            return new Value();
        }
    }

    // ===-----------------------------------------------------------------===
    // a function call consists of the function name, and the actual
    // argument. Note that all functions are unary.
    public static class Call extends Expr {
        private final Expr funExpr;
        private final Expr arg;

        public Call(Expr funExpr, Expr arg) {
            this.funExpr = funExpr;
            this.arg = arg;
            this.label = "Call `<fun> @ <arg>`";
            addChild(funExpr);
            addChild(arg);
        }

        @Override
        protected Value eval() {
            // TODO: students must complete this  // done
            if (!error) {
                // CODE HERE
                //get val
                //check if it matches
                //pass as arg
                Value functionVal = funExpr.eval();

                if (functionVal.getType() == Type.FUN_T) {
                    Lambda lambda = functionVal.getLambda();

                    String param = lambda.getVariable();
                    Value argValue = arg.eval();

                    table.bind(param, argValue);

                    // call body
                    lambda.getBody().exec();

                    if(!returnVariables.isEmpty())
                    {
                        return returnVariables.pop();
                    }

                }
            }
            return new Value();
        }
    }

    // ===-----------------------------------------------------------------===
    // this gives the type of what's stored in the Value object.
    public enum Type {
        NUM_T, BOOL_T, FUN_T, NONE_T;
    }

    public static class Value {
        private Object val;
        private final Type type;

        public Value() {
            type = Type.NONE_T;
        }

        public Value(int n) {
            val = n;
            type = Type.NUM_T;
        }

        public Value(boolean b) {
            val = b;
            type = Type.BOOL_T;
        }

        public Value(Lambda l) {
            val = l;
            type = Type.FUN_T;
        }

        public Type getType() {
            return type;
        }

        public int getNumber() {
            return (int) val;
        }

        public boolean getBoolean() {
            return (boolean) val;
        }

        public Lambda getLambda() {
            return (Lambda) val;
        }

        public void writeTo() {
//            System.out.println(val);
            switch (type) {
                case NUM_T, BOOL_T -> System.out.println(val);
                case FUN_T -> System.out.println("lambda expression");
                case NONE_T -> System.out.println("Unset value!");
            }
        }

        @Override
        public String toString() {
            return switch (type) {
                case NUM_T, BOOL_T -> String.valueOf(val);
                case FUN_T -> "lambda expression";
                case NONE_T -> "Unset value!";
            };
        }
    }

    // ===-----------------------------------------------------------------===
    // this class represents a simple global symbol table.
    public static class SymbolTable {
        private final Map<String, Stack<Value>> binding = new HashMap<>();
        private final Stack<Set<String>> scopeVariables = new Stack<>();

        public SymbolTable() { }

        // returns the value bound to the given name.
        Value lookup(final String name) {
            // TODO: write the implementation  //done
            //looks up the value
            //if not null peek
            //else throw error
            Stack<Value> lookedAt = binding.get(name);

            if(lookedAt != null){

                return lookedAt.peek();
            }
           else {
               error = true;
               System.out.println("ERROR: No binding for variable `" + name + "` exists!");
           }
            return new Value();
        }

        // create a new name-value binding.
        public void bind(final String name, Value val) {
            // TODO: write the implementation           //done
            //does it exist
            //push value to new stack
            //add to current scope
            //check if not current scope but here
            //add to current scope
            //error is current and present
            if(!binding.containsKey(name)){

                binding.put(name, new Stack<>());

                binding.get(name).push(val);

                scopeVariables.peek().add(name);
            }
            else if (!scopeVariables.peek().contains(name))
            {
                binding.get(name).push(val);

                scopeVariables.peek().add(name);
            }
            else
            {
                System.out.println("ERROR: Variable " + name + " already bound!");
            }
        }

        // re-defines the value bound to the given name.
        void rebind(final String name, Value val) {
            // TODO: write the implementation      //done
            // does it exist
            //then pop
            //push new val and get
            //else error
            if(binding.containsKey(name))
            {
                binding.get(name).pop();

                binding.get(name).push(val);
            }
            else {
              // error = true;
               System.out.println("ERROR: Cannot rebind `" + name + "` because it is not bound!");
           }
        }

        public void openScope() {
            // TODO: write the implementation           //done
            System.out.println("INFO: Opening scope!");
            // CODE HERE
            scopeVariables.push(new HashSet<>());
        }

        public void closeScope() {
            // TODO: write the implementation           //done
            System.out.println("INFO: Closing scope!");
            // CODE HERE
            // Set<String> pop = scopeVariables.pop();

            //remove current scope
            //iter through variable
            //add new value to top
            //check if not empty
            //pop if not
            //print statment
            //if empty remove
            //the error if not binded correctly
            Set<String> stackRemove = scopeVariables.pop();

            for (String variable : stackRemove) {
                Stack<Value> StackValuesToAdd = binding.get(variable);
                if (StackValuesToAdd != null && !StackValuesToAdd.isEmpty()) {
                    StackValuesToAdd.pop();
                    System.out.println("Removing '" + variable + "' from scope.");

                    if (StackValuesToAdd.isEmpty()) {
                        binding.remove(variable);
                    }
                } else {
                    error = true;
                    System.out.println("ERROR: Variable `" + variable + "` was not properly bound!");
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        Lexer lexer;
        Parser parser;
        Stmt stmt = null;
        Stmt ast = null;
        String input = null;
        Main.table.openScope();
        while (true) {
            Main.error = false;
            System.out.print("brain> ");
            input = reader.readLine();
            if ("quit".equals(input)) break;
            if ("tree".equals(input)) {
                if (ast != null)
                    System.out.println(PrettyPrinter.printAST(ast));
                continue;
            }
            lexer = new Lexer(input);
            List<Token> tokens = lexer.getTokens();
//            System.out.println(tokens);
            parser = new Parser(tokens);
            stmt = parser.init();
            if (ast == null) ast = stmt;
            else ast.addChild(stmt);
            if (stmt == null && !Main.error) break;
            else if (stmt != null)
                stmt.exec();
        }
        Main.table.closeScope();
        System.out.println("Good bye!");
    }
}