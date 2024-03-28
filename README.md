# Unpacker for Dean Edward's p.a.c.k.e.r

JavaScript unpacker to unpack/deobfuscate code packed with [Dean Edwards JavaScript's Packer](http://dean.edwards.name/packer/).

This project is based on [Einar Lielmanis' Python implementation](https://github.com/beautifier/js-beautify/blob/main/python/jsbeautifier/unpackers/packer.py).

## Implementation
The latest release is available on [Maven Central](https://central.sonatype.com/artifact/moe.nero/unpacker).

```gradle
implementation("moe.nero:unpacker:0.1.1")
```

## Usage

```kotlin
val packed = """
    eval(function(p,a,c,k,e,r){e=String;if(!''.replace(/^/,String)
    ){while(c--)r[c]=k[c]||c;k=[function(e){return r[e]}];e=
    function(){return'\\\\w+'};c=1};while(c--)if(k[c])p=p.replace(
    new RegExp('\\\\b'+e(c)+'\\\\b','g'),k[c]);return p}('0 2=1',62,3,'var||a'.split('|'),0,{}))
""".trimIndent()

// Determine whether the provided data is packed or not.
val isPacked = Unpacker.detect(packed) // Example output -> true

// Unpack the packed code.
val unpacked = Unpacker.unpack(packed) // Example output -> "var a=1"
```

## License
This project is licensed under the [MIT license](https://github.com/nerocchi/unpacker/blob/main/LICENSE).
