# Virtual Machine
The main goal of this project is to write structured, easily maintained, and well-documented code. The code itself
is a Virtual Machine that can run custom bytecode, as well as a custom lexer and parser for this bytecode.

The original idea for this project was [this section](http://gameprogrammingpatterns.com/bytecode.html) of a programming patterns book describing the "Bytecode" pattern.
I took the idea but went with a more object orientaded approach by operating on `Instruction` objects instead of bytes.
This means that even though I call it a "Bytecode Virtual Machine" it does not actually operate on bytes; it operates on `Instruction` objects. 
The name 'bytecode' sounds catchy and "tech-y" though so it stuck :)

## Running code on the virtual machine.
Running code on the virtual machine is easy:
```
Lexer lexer = new Lexer();
Parser parser = new Parser();

// Hardcoded
String code = "literal 5 \n literal 7 \n add"; // Adds 5 to 7
// Read from file
String code = new String(Files.readAllBytes("dir/to/your/file.anyextention"));

List<Token> tokens = lexer.tokenize(code);
Bytecode bytecode = parser.parse(tokens);
VirtualMachine vm = new DefaultVirtualMachine(64, 128); // Arbitrary max stack and variable size

vm.execute(bytecode); // That's it!
```

## Using the GUI
You can also use the minimal GUI to run and debug your code. Note that the GUI is only there for visual assitance; it is a very 
weak tool and should only really be used if you need to see variables and stack usage while running your code. Also useful for stepping through code execution.
You can open the GUI by running the `main` method inside `GUI.java`. E.g. `java -jar GUI.java` in your command window.

## Points of interest
Use the "Find File" function (present in "Files" tab in Gitlab and "Code" tab in Github) to quickly find the files mentioned below.

**Documentation:** *VirtualMachine.java*. 
Generally well-documented file, because it is important to know what is defined and undefined behavior for the virtual machine and what guarantees it can make the end-user.

**Code Style:** *DefaultVirtualMachine.java*.
The main bulk of code in the project. Focus on making self-documenting and readable code.

**Lexing:** *Lexer.java*. 
Uses regex to convert text into tokens: `#define HELLO = 1` -> `TOKEN_DEFTAG TOKEN_DEFINE TOKEN_ID TOKEN_EQUALS TOKEN_NUMBER`.

**Parsing:** *Parser.java*.
The parser turns a stream of tokens (supplied by the lexer) into several `Instruction` objects that the Virtual Machine can then execute.

**Instruction descriptions:** *Language Specification.docx*.
Document describing all valid instructions and what they do.

**Language grammar:** *grammar.gr*.
Text file describing the tokens the Lexer uses and the rules the Parser follows.

**Example code:** *count_primes.vm*.
This file contains some example code that counts the number of primes from 1-1000. 
The Java code equivalent is also present in that file, in case the bytecode is confusing.