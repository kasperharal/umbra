# vertion 1.0 umbra
## consepts
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
# vertion 1.1 file variables
## consepts
- adds filevars byte arrays keyed by strings
## features
- `\$varname\` sets filevar $varname to linevar $varname: any string
- `/$varname/` sets arg register to filevar $varname $varname: any string
# vertion 1.2 utils
## consepts
- adds long needed utils
## features
- `|` sets linevar to linevar byte at arg register
- `<$char>` add $char byte value to arg register $char: any ascii Character
- `!=` clears linevar
- `I=` sets linevar to loop register
# vertion 1.2 conditonals
## consepts
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
# vertion 1.3 global variables
## consepts
- adds projectvars byte arrays keyed by strings
- projectvars can be accesed by all modules
## features
- `load<$varname>` sets projectvar $varname to linevar $varname: any string
- `save<$varname>` sets arg register to projectvar $varname $varname: any string
# vertion 1.4.0 lambdas
## consepts
- adds lambdas
- $lambda can by any scope code
## features
- `?e:($lambda)` runs lambda if last of linevar =  arg register
- `?l:($lambda)` runs lambda if last of linevar <=  arg register
- `?L:($lambda)` runs lambda if last of linevar <  arg register
- `?g:($lambda)` runs lambda if last of linevar =>  arg register
- `?G:($lambda)` runs lambda if last of linevar >  arg register
- `!e:($lambda)` runs lambda if not last of linevar =  arg register
- `!l:($lambda)` runs lambda if not last of linevar <= arg register
- `!L:($lambda)` runs lambda if not last of linevar <  arg register
- `!g:($lambda)` runs lambda if not last of linevar => arg register
- `!G:($lambda)` runs lambda if not last of linevar >  arg register
# vertion 1.4.1 lambdas variables
## consepts
- adds lambdavars keyed by strings
- $lambda can also be any lambdavar key
## features
- `?e:($lambda)` runs lambda if last of linevar =  arg register
- `?l:($lambda)` runs lambda if last of linevar <=  arg register
- `?L:($lambda)` runs lambda if last of linevar <  arg register
- `?g:($lambda)` runs lambda if last of linevar =>  arg register
- `?G:($lambda)` runs lambda if last of linevar >  arg register
- `!e:($lambda)` runs lambda if not last of linevar =  arg register
- `!l:($lambda)` runs lambda if not last of linevar <= arg register
- `!L:($lambda)` runs lambda if not last of linevar <  arg register
- `!g:($lambda)` runs lambda if not last of linevar => arg register
- `!G:($lambda)` runs lambda if not last of linevar >  arg register
