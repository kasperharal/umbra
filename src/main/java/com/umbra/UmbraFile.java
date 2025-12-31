package com.umbra;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class UmbraFile {
    String[] code;
    int lineindex = 0;
    Value valuearg = null;
    Value callArg = new Value();

    String fileBuffer = ""; 
    String filePath = ""; 

    HashMap<Integer, Value> vars = new HashMap<>();
    HashMap<String, String> lambdas = new HashMap<>();
    ArrayDeque<Integer> loopStackPoint = new ArrayDeque<>();
    ArrayDeque<Integer> loopStack = new ArrayDeque<>();

    HashMap<String, Value> fileVar = new HashMap<>();

    HashMap<String, Scope> scopes = new HashMap<>();


    public UmbraFile(String name, Path File) throws IOException {
        this(name, Files.readString(File));
    }

    public UmbraFile(String name, String File) throws IOException {
        code = File.split("(\\s#.*\n|\n)");
        String scope = "";
        Integer scopebegin = null;
        for (lineindex = 0; lineindex < code.length; lineindex++) {
            if (code[lineindex].matches("imp<.+?>")) {
                UmbraProject.addModule(trim(code[lineindex], 4));
                code[lineindex] = "";
            } else if (code[lineindex].matches("scp<[a-z]+> \\{")) {
                scope = trim(code[lineindex], 4, 3);
                scopebegin = lineindex;
                code[lineindex] = "";
            } else if (code[lineindex].matches("scp<[a-z]+> \\{\\}")) {
                scopes.put(trim(code[lineindex], 4, 4), new Scope(lineindex, lineindex));
                code[lineindex] = "";
            } else if (code[lineindex].matches("\\}") && scopebegin != null) {
                scopes.put(scope, new Scope(scopebegin, lineindex));
                code[lineindex] = "";
            }
        }
        runScope(name);
    }

    public void runScope(String scope) {
        //System.out.println(scope);
        //scopes.forEach((k,v)->System.out.println(k+":"+v));
        for (lineindex = scopes.get(scope).start; lineindex < scopes.get(scope).end; lineindex++) {
            if (runCode(code[lineindex].trim())) {
                break;
            }
        }
    }

    public Value runScope(String scope, Value value) {
        callArg = value;
        Value retVal = new Value();
        for (lineindex = scopes.get(scope).start; lineindex < scopes.get(scope).end; lineindex++) {
            if (runCode(code[lineindex].trim())) {
                retVal = vars.get(lineindex);
                break;
            }
        }
        callArg = new Value();
        return retVal;
    }

    public boolean runCode(String line) {
        valuearg = new Value();
        String buffer = "";
        for (int index = 0; index < line.length(); index++) {
            buffer += line.charAt(index);
            if (buffer.matches("<-?\\d+>")) {
                valuearg.add(Byte.parseByte(trim(buffer)));
                buffer = "";
            } else if (buffer.matches("<.>")) {
                valuearg.add((byte)trim(buffer).charAt(0));
                buffer = "";
            } else if (buffer.matches("<\\\\[\\\\nrtfb0#]>")) {
                if (trim(buffer).equals("\\n")) valuearg.add((byte)'\n');
                else if (trim(buffer).equals("\\r")) valuearg.add((byte)'\r');
                else if (trim(buffer).equals("\\t")) valuearg.add((byte)'\t');
                else if (trim(buffer).equals("\\f")) valuearg.add((byte)'\f');
                else if (trim(buffer).equals("\\b")) valuearg.add((byte)'\b');
                else if (trim(buffer).equals("\\0")) valuearg.add((byte)'\0');
                else if (trim(buffer).equals("\\#")) valuearg.add((byte)'#');
                else if (trim(buffer).equals("\\\\")) valuearg.add((byte)'\\');
                buffer = "";
            } else if (buffer.matches("<\\\\(ls|gr)\\\\>")) {
                if (trim(buffer).equals("\\gr\\")) valuearg.add((byte)'>');
                else if (trim(buffer).equals("\\gr\\")) valuearg.add((byte)'<');
                buffer = "";
            } else if (buffer.matches("<<.*?>>")) {
                buffer = buffer.replace("\\n", "\n");
                buffer = buffer.replace("\\r", "\r");
                buffer = buffer.replace("\\t", "\t");
                buffer = buffer.replace("\\f", "\f");
                buffer = buffer.replace("\\b", "\b");
                buffer = buffer.replace("\\0", "\0");
                buffer = buffer.replace("\\#", "#");
                buffer = buffer.replace("\\\\", "\\");
                buffer = buffer.replace("\\gr\\", ">");
                buffer = buffer.replace("\\ls\\", "<");
                valuearg = new Value(trim(buffer,2,2).getBytes());
                buffer = "";
            } else if (buffer.matches("<>")) {
                valuearg.add(vars.get(lineindex));
                buffer = "";
            } else if (buffer.matches("=")) {
                vars.put(lineindex, new Value(valuearg));
                valuearg = new Value();
                buffer = "";
            } else if (buffer.matches("!=")) {
                vars.put(lineindex, new Value());
                buffer = "";
            } else if (buffer.matches("&=")) {
                vars.get(lineindex).add(valuearg);
                valuearg = new Value();
                buffer = "";
            } else if (buffer.matches("I=")) {
                vars.get(lineindex).add(loopStack.peek().byteValue());
                valuearg = new Value();
                buffer = "";
            } else if (buffer.matches("A=")) {
                vars.put(lineindex, callArg);
                valuearg = new Value();
                buffer = "";
            } else if (buffer.matches("/[a-z]+/")) {
                valuearg = fileVar.get(trim(buffer));
                buffer = "";
            } else if (buffer.matches("\\\\[a-z]+\\\\")) {
                fileVar.put(trim(buffer), vars.get(lineindex));
                buffer = "";
            } else if (buffer.matches("\\+")) {
                Value newVar = vars.get(lineindex);
                for (int i = 0; i < newVar.size(); i++) {
                    newVar.set(i, (byte)(newVar.get(i)+1));
                }
                buffer = "";
            } else if (buffer.matches("-")) {
                Value newVar = vars.get(lineindex); 
                for (int i = 0; i < newVar.size(); i++) {
                    newVar.set(i, (byte)(newVar.get(i)-1));
                }
                buffer = "";
            } else if (buffer.matches("\\|")) {
                vars.put(lineindex, new Value(vars.get(lineindex).get(valuearg.get(0))));
                valuearg = new Value();
                buffer = "";
            } else if (buffer.matches("\\[")) {
                loopStackPoint.add(index);
                loopStack.add(0);
                buffer = "";
            } else if (buffer.matches("\\]")) {
                int loopLoops = loopStack.pop();
                if (loopLoops < valuearg.get(0)-1) {
                    loopStack.push(loopLoops+1);
                    index = loopStackPoint.peek();
                } else {
                    loopStackPoint.pop();
                }
                valuearg = new Value();
                buffer = "";
            } else if (buffer.matches("\\?[elLgG]\\]")) {
                int loopLoops = loopStack.pop();
                if (trim(buffer).equals("e")) {
                    if (vars.get(lineindex).get(vars.get(lineindex).size()-1) == valuearg.get(0)) {
                        loopStack.push(loopLoops+1);
                        index = loopStackPoint.peek();
                    } else {
                        loopStackPoint.pop();
                    }
                } else if (trim(buffer).equals("l")) {
                    if (vars.get(lineindex).get(vars.get(lineindex).size()-1) <= valuearg.get(0)) {
                        loopStack.push(loopLoops+1);
                        index = loopStackPoint.peek();
                    } else {
                        loopStackPoint.pop();
                    }
                } else if (trim(buffer).equals("L")) {
                    if (vars.get(lineindex).get(vars.get(lineindex).size()-1) < valuearg.get(0)) {
                        loopStack.push(loopLoops+1);
                        index = loopStackPoint.peek();
                    } else {
                        loopStackPoint.pop();
                    }
                } else if (trim(buffer).equals("g")) {
                    if (vars.get(lineindex).get(vars.get(lineindex).size()-1) >= valuearg.get(0)) {
                        loopStack.push(loopLoops+1);
                        index = loopStackPoint.peek();
                    } else {
                        loopStackPoint.pop();
                    }
                } else if (trim(buffer).equals("G")) {
                    if (vars.get(lineindex).get(vars.get(lineindex).size()-1) > valuearg.get(0)) {
                        loopStack.push(loopLoops+1);
                        index = loopStackPoint.peek();
                    } else {
                        loopStackPoint.pop();
                    }
                }
                valuearg = new Value();
                buffer = "";
            } else if (buffer.matches("\\![elLgG]\\]")) {
                int loopLoops = loopStack.pop();
                if (trim(buffer).equals("e")) {
                    if (vars.get(lineindex).get(vars.get(lineindex).size()-1) != valuearg.get(0)) {
                        loopStack.push(loopLoops+1);
                        index = loopStackPoint.peek();
                    } else {
                        loopStackPoint.pop();
                    }
                } else if (trim(buffer).equals("l")) {
                    if (vars.get(lineindex).get(vars.get(lineindex).size()-1) > valuearg.get(0)) {
                        loopStack.push(loopLoops+1);
                        index = loopStackPoint.peek();
                    } else {
                        loopStackPoint.pop();
                    }
                } else if (trim(buffer).equals("L")) {
                    if (vars.get(lineindex).get(vars.get(lineindex).size()-1) >= valuearg.get(0)) {
                        loopStack.push(loopLoops+1);
                        index = loopStackPoint.peek();
                    } else {
                        loopStackPoint.pop();
                    }
                } else if (trim(buffer).equals("g")) {
                    if (vars.get(lineindex).get(vars.get(lineindex).size()-1) < valuearg.get(0)) {
                        loopStack.push(loopLoops+1);
                        index = loopStackPoint.peek();
                    } else {
                        loopStackPoint.pop();
                    }
                } else if (trim(buffer).equals("G")) {
                    if (vars.get(lineindex).get(vars.get(lineindex).size()-1) <= valuearg.get(0)) {
                        loopStack.push(loopLoops+1);
                        index = loopStackPoint.peek();
                    } else {
                        loopStackPoint.pop();
                    }
                }
                valuearg = new Value();
                buffer = "";
            } else if (buffer.matches("\\?[elLgG]:.*?\\)")) {
                if (buffer.charAt(1) == 'e') {
                    if (vars.get(lineindex).get(vars.get(lineindex).size()-1) == valuearg.get(0)) {
                        runCode(getLambda(buffer));
                    }
                } else if (buffer.charAt(1) == 'l') {
                    if (vars.get(lineindex).get(vars.get(lineindex).size()-1) <= valuearg.get(0)) {
                        runCode(getLambda(buffer));
                    }
                } else if (buffer.charAt(1) == 'L') {
                    if (vars.get(lineindex).get(vars.get(lineindex).size()-1) < valuearg.get(0)) {
                        runCode(getLambda(buffer));
                    }
                } else if (buffer.charAt(1) == 'g') {
                    if (vars.get(lineindex).get(vars.get(lineindex).size()-1) >= valuearg.get(0)) {
                        runCode(getLambda(buffer));
                    }
                } else if (buffer.charAt(1) == 'G') {
                    if (vars.get(lineindex).get(vars.get(lineindex).size()-1) > valuearg.get(0)) {
                        runCode(getLambda(buffer));
                    }
                }
                valuearg = new Value();
                buffer = "";
            } else if (buffer.matches("\\![elLgG]:.*?\\)")) {
                if (buffer.charAt(1) == 'e') {
                    if (vars.get(lineindex).get(vars.get(lineindex).size()-1) != valuearg.get(0)) {
                        runCode(getLambda(buffer));
                    }
                } else if (buffer.charAt(1) == 'l') {
                    if (vars.get(lineindex).get(vars.get(lineindex).size()-1) > valuearg.get(0)) {
                        runCode(getLambda(buffer));
                    }
                } else if (buffer.charAt(1) == 'L') {
                    if (vars.get(lineindex).get(vars.get(lineindex).size()-1) >= valuearg.get(0)) {
                        runCode(getLambda(buffer));
                    }
                } else if (buffer.charAt(1) == 'g') {
                    if (vars.get(lineindex).get(vars.get(lineindex).size()-1) < valuearg.get(0)) {
                        runCode(getLambda(buffer));
                    }
                } else if (buffer.charAt(1) == 'G') {
                    if (vars.get(lineindex).get(vars.get(lineindex).size()-1) <= valuearg.get(0)) {
                        runCode(getLambda(buffer));
                    }
                }
                valuearg = new Value();
                buffer = "";
            } else if (buffer.matches("do:.*?\\)")) {
                runCode(getLambda(buffer));
                buffer = "";
            } else if (buffer.matches("lambda<[a-zA-Z]+>:\\(.*?\\)")) {
                lambdas.put(buffer.substring(buffer.indexOf('<')+1, buffer.indexOf('>'))+"()", getLambda(buffer));
                buffer = "";
            } else if (buffer.matches("prt")) {
                if (!vars.get(lineindex).isEmpty()) {
                    if (valuearg.isEmpty()) {
                        if (vars.get(lineindex).size() <= valuearg.get(0) || valuearg.get(0) < 0) 
                            System.out.println(vars.get(lineindex).get(0));
                        else System.out.println(vars.get(lineindex).get(valuearg.get(0)));
                    } else {
                        System.out.println(vars.get(lineindex).get(0));
                    }
                }
                valuearg = new Value();
                buffer = "";
            } else if (buffer.matches("pst")) {
                System.out.print(new String(vars.get(lineindex).getChar()));
                buffer = "";
            } else if (buffer.matches("gul")) {
                valuearg = new Value(UmbraProject.input.nextLine().getBytes());
                buffer = "";
            } else if (buffer.matches("open<.+?>")) {
                filePath = trim(buffer, 5);
                try {
                    fileBuffer = Files.readString(Path.of(filePath));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                buffer = "";
            } else if (buffer.matches("close")) {
                try {
                    Files.deleteIfExists(Path.of(filePath));
                    Files.writeString(Path.of(filePath), fileBuffer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                buffer = "";
            } else if (buffer.matches("frl")) {
                vars.put(lineindex, new Value(fileBuffer.split("\\n")[valuearg.get(0)].getBytes()));
                valuearg = new Value();
                buffer = "";
            } else if (buffer.matches("fra")) {
                vars.put(lineindex, new Value(fileBuffer.getBytes()));
                buffer = "";
            } else if (buffer.matches("fwt")) {
                fileBuffer = new String(vars.get(lineindex).getChar());
                buffer = "";
            } else if (buffer.matches("fap")) {
                fileBuffer += new String(vars.get(lineindex).getChar());
                buffer = "";
            } else if (buffer.matches("load<.+?>")) {
                valuearg = UmbraProject.projectVar.get(trim(buffer, 5));
                buffer = "";
            } else if (buffer.matches("save<.+?>")) {
                UmbraProject.projectVar.put(trim(buffer, 5), vars.get(lineindex));
                buffer = "";
            } else if (buffer.matches("esp<.+?>")) {
                String arg = trim(buffer, 4);
                if (arg.contains(".")) {
                    UmbraProject.modules.get(arg.substring(0, arg.indexOf('.'))).runScope(arg.substring(arg.indexOf('.')+1));
                } else {
                    runScope(arg);
                }
                buffer = "";
            } else if (buffer.matches("call<.+?>")) {
                String arg = trim(buffer, 5);
                if (arg.contains(".")) {
                    valuearg = UmbraProject.modules.get(arg.substring(0, arg.indexOf('.'))).runScope(arg.substring(arg.indexOf('.')+1), vars.get(lineindex));
                } else {
                    valuearg = runScope(arg, vars.get(lineindex));
                }
                buffer = "";
            } else if (buffer.matches("ret")) {
                return true;
            } else if (buffer.matches("cast:.*?\\)")) {
                if (buffer.matches("cast:\\(.*?\\)")) valuearg = new Value(getLambda(buffer).getBytes());
                else if (lambdas.containsKey(buffer.substring(buffer.indexOf(":")+1, buffer.lastIndexOf(')')+1))) {
                    valuearg = new Value(getLambda(buffer).getBytes());
                } else {
                    lambdas.put(buffer.substring(buffer.indexOf(":")+1, buffer.lastIndexOf(')')+1), new String(vars.get(lineindex).getChar()));
                }
                buffer = "";
            }
        }
        return false;
    }

    public String trim(String in) {
        return in.substring(1, in.length()-1);
    }

    public String trim(String in, int off) {
        return in.substring(off, in.length()-1);
    }

    public String trim(String in, int off, int end) {
        return in.substring(off, in.length()-end);
    }

    public String getLambda(String lambda) {
        String out = lambda.substring(lambda.indexOf(":")+1, lambda.lastIndexOf(')')+1);
        if (lambdas.containsKey(out)) {
            return lambdas.get(out);
        }
        out = out.substring(1, out.length()-1);
        return out;
    }

    record Scope(int start, int end) {}

    class Value {
        private ArrayList<Byte> values = new ArrayList<>();

        public Value(byte... bytes) {
            for (byte element : bytes) {
                values.add(element);
            }
        }

        public Value(Value value) {
            this();
            for (byte element : value.get()) {
                values.add(element);
            }
        }

        public void add(byte value) {
            values.add(value);
        }

        public void add(Value value) {
            values.addAll(value.get());
        }

        public void set(int index, byte value) {
            values.set(index, value);
        }

        public List<Byte> get() {
            return values;
        }

        public byte get(int index) {
            return values.get(index);
        }

        public char[] getChar() {
            char[] chars = new char[values.size()];
            for (int index = 0; index < chars.length; index++) {
                chars[index] = (char)values.get(index).byteValue();
            }
            return chars;
        }

        public boolean isEmpty() {
            return values.isEmpty();
        }

        public int size() {
            return values.size();
        }
    }
}