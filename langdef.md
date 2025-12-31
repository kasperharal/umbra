# version 1.0 umbra
## concepts
- umbra is a esolang where all lines has its own byte array called linevar
- all code that is not taged with (not scope code) must be written in a scope
- umbra has two registers arg register and loop register
- arg register is a byte array that is cleared every time it is used
- loop register only exist inside loops and count how many times the loop has looped (private)
## features
- `#` line comment
- `imp<$arg>` imports a file as module $arg: filepath (not scope code)
- `scp<$arg> {$arg}` creates a scope between `{}` $arg: scopename (not scope code)
- `esp<$arg>` executes scope $arg: scopename | module.scopename
- `<$byte>` add $byte to arg register $byte: any byte
- `=` sets linevar to arg register
- `&=` appends arg register to linevar
- `+` increments all bytes in linevar
- `-` decrement all bytes in linevar
- `[$code]` loops $code until loop register = arg register at `]` $code: any scope code
- `prt` prints linevar byte at arg register or linevar byte at 0
- `pst` prints linevar as string
# version 1.1 file variables
## concepts
- adds filevars byte arrays keyed by strings
## features
- `\$varname\` sets filevar $varname to linevar $varname: any string
- `/$varname/` sets arg register to filevar $varname $varname: any string
# version 1.2 utils
## concepts
- adds long needed utils
## features
- `|` sets linevar to linevar byte at arg register
- `<$char>` add $char byte value to arg register $char: any utf-8 Character
- `!=` clears linevar
- `I=` sets linevar to loop register
# version 1.2 conditonals
## concepts
- adds conditonal loops
## features
- `[$code?e]` loops $code until last of linevar =  arg register at `]` $code: any scope code
- `[$code?l]` loops $code until last of linevar <= arg register at `]` $code: any scope code
- `[$code?L]` loops $code until last of linevar <  arg register at `]` $code: any scope code
- `[$code?g]` loops $code until last of linevar => arg register at `]` $code: any scope code
- `[$code?G]` loops $code until last of linevar >  arg register at `]` $code: any scope code
- `[$code!e]` loops $code while last of linevar =  arg register at `]` $code: any scope code
- `[$code!l]` loops $code while last of linevar <= arg register at `]` $code: any scope code
- `[$code!L]` loops $code while last of linevar <  arg register at `]` $code: any scope code
- `[$code!g]` loops $code while last of linevar => arg register at `]` $code: any scope code
- `[$code!G]` loops $code while last of linevar >  arg register at `]` $code: any scope code
# version 1.3 global variables
## concepts
- adds projectvars byte arrays keyed by strings
- projectvars can be accesed by all modules
## features
- `load<$varname>` sets projectvar $varname to linevar $varname: any string
- `save<$varname>` sets arg register to projectvar $varname $varname: any string
# version 1.4.0 lambdas
## concepts
- adds lambdas
- $lambda can run any scope code
## features
- `:($lambda)` creates naw lambda
- `?e:$lambda` runs $lambda if last of linevar =  arg register
- `?l:$lambda` runs $lambda if last of linevar <=  arg register
- `?L:$lambda` runs $lambda if last of linevar <  arg register
- `?g:$lambda` runs $lambda if last of linevar =>  arg register
- `?G:$lambda` runs $lambda if last of linevar >  arg register
- `!e:$lambda` runs $lambda if not last of linevar =  arg register
- `!l:$lambda` runs $lambda if not last of linevar <= arg register
- `!L:$lambda` runs $lambda if not last of linevar <  arg register
- `!g:$lambda` runs $lambda if not last of linevar => arg register
- `!G:$lambda` runs $lambda if not last of linevar >  arg register
# version 1.4.1 lambdas variables
## concepts
- adds lambdavars keyed by  `[a-zA-Z]+`
- $lambda can also be any lambdavar key
## features
- `do:$lambda` runs $lambda
- `lambda<$name()>:$lambda` sets lambdavars $name to $lambda ($name must end with "( )")
# version 1.5 advanced utils
## concepts
- adds some more complex utils
- adds file io
- adds escape sequnces [`\n`, `\r`, `\t`, `\f`, `\b`, `\0`, `\#`, `\\`, `\ls\`, `\gr\`] all chars can be replaced with escape sequnces
## features
- `<<$string>>` sets arg register to bytes of $string $string: char array
- `gul` sets arg register to console input
- `open<$path>` opens file of $path $path: file path
- `close` writes file buffer to file
- `frl` reads line arg register from file to linevar
- `fra` reads intire file to linevar
- `rwt` writes linevar to file
- `fap` appends linevar to file
# version 1.6 functions
## concepts
- adds scope exectuion with return and arg
## features
- `call<$scope>` executes $scope with linevar as arg $scope: any scope
- `A=` gets arg value
- `ret` returns from call with linevar into arg register
# version 1.7 importing and casting
- adds import filepath utils
- adds casting from and to lambda
## features
- `imp</$filepath>` imports $filepath at project root $filepath: any path relative to project root
- `imp<@$filepath>` imports $filepath from gitlib $filepath: any path from gitlib
- `cast:$lambda` if $lambda exist then cast $lambda as string into byte array  
else cast linevar as string into lambdavar with key $lambda