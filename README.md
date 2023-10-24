# CustomDynamicLanguage: Brain
A Java application has been developed to support a dynamic language that users can interact with via the command line. 
Users can initialize their own variables and functions in real time due to dynamic typing. 
We've crafted our own syntax using a custom parser. 
The program supports features such as while loops, if-else statements, function calls, and more.
## Functionality
The program functions similarly to C++ and Java but with many restrictions. Here is a list of some of the functions it supports. 
-Integers and booleans
-Basic arithmetic (+, -, *, and /)
-Numeric comparisons (=, !=, >, >=, <, and <=)
-Boolean operators (&&, ||, !&)
-Input/output (read and write)
-Control structures (if, ifelse, and while)
-Variables, declared with keyword and assigned using := operator
-User-defined unary functions defined with lambda statements

##Sample Run Commands 
Test the functionality of the lambda function
`auto f := lambda x { write x + 5; };`
`f@4;`
Test's functionality of Control Structures such as "if", "ifelse" & "while"
`{auto x := 1; if x = 1 { auto x := 2; write x; } write x;};`
`auto f := lambda x { ifelse x <= 1 { ret := 1; } { ret := x * f @(n - 1); } };`

Test's functionality of Control Structures + lambda function
`auto fact := lambda n { ifelse n <= 1 { ret := 1; } { ret := n * fact@(n - 1); } };`
`write fact@5;`

Test's functionality of all of the supported function 
`auto other := lambda k { auto prod := 1; auto i := 1; while i <= k { prod := prod * i; i := i + 1; } ret := prod; };`

Test's a simple loop control
`auto i := 1;`
`auto sum := 0;`
`while i <= 100 { sum := sum + i; i := i + 1; }`
`write sum;`
