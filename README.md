# RSCM Replacer (Int → String)

This plugin replaces hardcoded or Constant/constant variable `int` values with their corresponding **RSCM string keys** wherever it can confidently resolve them.

## Usage

Install plugin

Add a RSCM annotation structured like this

```kotlin
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Rscm(val value: String)
```

Use the annotation to annotate methods with the rscm prefix you are interested in

<img width="725" height="179" alt="image" src="https://github.com/user-attachments/assets/69e77ea1-a6c3-427c-9220-fe56c1a93838" />

After running, the rscm values will replace the old int ones where possible.

<img width="816" height="497" alt="image" src="https://github.com/user-attachments/assets/95ca7f6e-850a-4dc3-9e1b-16cc572657be" />


Shortcomings:

Hardcoded RSCM path. Currently set to "C:\\Users\\Home\\Downloads\\rscm\\"

Uses old RSCM format. RSCM now supports subtypes. This doesnt and uses the old format of splitting strings with : rather than the = like now.

Does not update the calling method, after changing the user needs to properly update the code to handle the Strings instead of ints.
