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


## Configuration

Open **Settings/Preferences → Tools → RSCM Import Settings** to point the plugin at the folder containing your `.rscm` files. The configured path is persisted between IDE restarts and can be updated at any time.

## Updating the packaged plugin

Trigger the **Build and commit plugin artifact** GitHub Actions workflow to rebuild the plugin ZIP via Gradle and commit the refreshed `artifacts/plugin.zip` back to the repository. The workflow is manual (`workflow_dispatch`) to avoid noisy commits and will skip the commit when no artifact changes are detected. Locally, run `./gradlew copyPluginZip` to produce the same `artifacts/plugin.zip` file for review.

## Known gaps

- Uses old RSCM format. RSCM now supports subtypes. This doesnt and uses the old format of splitting strings with : rather than the = like now.
- Does not update the calling method, after changing the user needs to properly update the code to handle the Strings instead of ints.
