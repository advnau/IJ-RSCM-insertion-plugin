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

                // Find all parameters of type Int
                val intParameters = function.valueParameters.filter { it.typeReference?.text == "Int" }

                if (intParameters.isNotEmpty()) {
                    // Register a problem for each Int parameter (on the full KtParameter element)
                    for (parameter in intParameters) {
                        holder.registerProblem(
                            parameter,  // Register the problem on the full parameter element
                            "Replace all calls with String value",
                            ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
                            ReplaceIntWithMappedStringQuickFix(function.name ?: "", parameter.name ?: "")
                        )
                    }
                }
            }
        }
    }
}