package com.example.demo

import com.intellij.codeInspection.*
import com.intellij.psi.PsiElementVisitor
import org.jetbrains.kotlin.idea.inspections.AbstractKotlinInspection
import org.jetbrains.kotlin.psi.*

class ReplaceIntWithMappedStringInspection : AbstractKotlinInspection() {

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : KtVisitorVoid() {
            override fun visitNamedFunction(function: KtNamedFunction) {
                super.visitNamedFunction(function)

                // Extract the map from the @MagicConstant annotation
                val replacementMap = extractReplacementMap(function)
                if (replacementMap == null) return
                // Find all parameters of type Int
                val intParameters = function.valueParameters.filter { it.typeReference?.text == "Int" }

                if (intParameters.isNotEmpty()) {
                    // Register a problem for each Int parameter (on the full KtParameter element)
                    for (parameter in intParameters) {
                        holder.registerProblem(
                            parameter,  // Register the problem on the full parameter element
                            "Replace all calls with String value",
                            ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
                            ReplaceIntWithMappedStringQuickFix(function.name ?: "", parameter.name ?: "", replacementMap)
                        )
                    }
                }
            }
        }
    }

    // Function to extract the map from the @MagicConstant annotation using PSI
    private fun extractReplacementMap(function: KtNamedFunction): Map<Int, String>? {
        // Find the annotation in the function
        val magicConstantAnnotation = function.annotationEntries.find {
            it.shortName?.asString() == "MagicConstant"
        } ?: return null

        // Extract the stringValues from the annotation arguments
        val stringValues = getStringValuesFromAnnotation(magicConstantAnnotation) ?: return null

        // Assuming the strings are in "Int:String" format
        return stringValues.mapNotNull { entry ->
            val (key, value) = entry.split(":")
            key.toIntOrNull()?.let { intKey -> intKey to value }
        }.toMap()
    }

    // Extract the "stringValues" from the annotation entry
    private fun getStringValuesFromAnnotation(annotation: KtAnnotationEntry): List<String>? {
        // Find the argument list and extract "stringValues"
        val valueArguments = annotation.valueArguments

        // Find the "stringValues" argument
        for (arg: ValueArgument in valueArguments) {
            if (arg.getArgumentName()?.asName?.asString() == "stringValues") {
                // Extract the values from the array initializer
                val expression = arg.getArgumentExpression()?.text
                return expression?.removeSurrounding("[", "]")?.split(",")?.map { it.trim('"', ' ') }
            }
        }
        return null
    }
}