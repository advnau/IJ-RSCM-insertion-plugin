package com.example.demo

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.searches.ReferencesSearch
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.idea.caches.resolve.analyze
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.psi.KtVariableDeclaration
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.resolve.lazy.BodyResolveMode
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.idea.references.resolveMainReferenceToDescriptors
import org.jetbrains.kotlin.descriptors.VariableDescriptor
import org.jetbrains.kotlin.js.resolve.diagnostics.findPsi
import org.jetbrains.kotlin.psi.KtReferenceExpression
import org.jetbrains.kotlin.psi.psiUtil.forEachDescendantOfType

class ReplaceIntWithMappedStringQuickFix(
    private val functionName: String,
    private val parameterName: String
) : LocalQuickFix {

    // Map of Int to String for replacement
    private val intToStringMap = mapOf(123 to "oneTwoThree", 124 to "oneTwoFour")

    override fun getName(): String = "Replace Int arguments with mapped String in all calls"

    override fun getFamilyName(): String = "Replace Int arguments with mapped String"

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val element = descriptor.psiElement

        // Check if the element is a KtParameter
        //println("DEBUG: Element type: ${element.javaClass.simpleName}")
        if (element !is KtParameter) {
            //println("DEBUG: Element is not a KtParameter, exiting.")
            return
        }

        //println("DEBUG: Processing parameter: ${element.name}")

        // Find the function that contains this parameter
//        val function = element.parent as? KtNamedFunction ?: return
        val ownerFunction = element.ownerFunction
        val function = if (ownerFunction is KtNamedFunction) {
            ownerFunction
        }
        else {
            //println("DEBUG: ownerFunction is not a KtNamedFunction, exiting.")
            return
        }

        // Find the index of the parameter
        val parameterIndex = function.valueParameters.indexOf(element)
        if (parameterIndex == -1) {
            //println("DEBUG: Failed to find parameter in the function, exiting.")
            return
        }

        //println("DEBUG: Found function: ${function.name} with parameter index: $parameterIndex")


        // Search for all references to this function in the entire project scope
        val references = ReferencesSearch.search(function, GlobalSearchScope.projectScope(project)).findAll()

        //println("DEBUG: Found ${references.size} references to function $functionName")

        // Iterate over all references to the function
        for (reference in references) {
            val functionCall = reference.element.parent as? KtCallExpression ?: continue

            //println("DEBUG: Processing function call: ${functionCall.text}")

            // Get the argument at the same index as the parameter
            val argument = functionCall.valueArguments.getOrNull(parameterIndex) ?: continue
            val argumentExpression = argument.getArgumentExpression()

            // Check if the argument is an Int constant
            when (argumentExpression) {
                is KtConstantExpression -> {
                    // Handle direct Int constants
                    replaceConstant(argumentExpression, parameterIndex, functionCall)
                }
                is KtNameReferenceExpression -> {
                    println("DEBUG: Attempting to replace the variable.")
                    // Handle variable references
                    replaceVariable(argumentExpression, parameterIndex, functionCall)
                }
                else -> {
                    println("DEBUG: Argument is not a constant or variable reference.")
                }
            }
        }
    }

    // Replace direct Int constants
    private fun replaceConstant(argumentExpression: KtConstantExpression, parameterIndex: Int, functionCall: KtCallExpression) {
        val intValue = argumentExpression.text.toIntOrNull()
        //println("DEBUG: Argument value at index $parameterIndex: $intValue")

        if (intValue != null && intToStringMap.containsKey(intValue)) {
            val newValue = intToStringMap[intValue]
            //println("DEBUG: Replacing Int value $intValue with String \"$newValue\"")

            // Replace the Int value with the corresponding String value
            val newExpression = KtPsiFactory(functionCall).createExpression("\"$newValue\"")
            argumentExpression.replace(newExpression)
            //println("DEBUG: Replacement successful!")
        } else {
            //println("DEBUG: No corresponding String value found for Int $intValue.")
        }
    }

    // Replace variables that are initialized with Int constants
    private fun replaceVariable(argumentExpression: KtNameReferenceExpression, parameterIndex: Int, functionCall: KtCallExpression) {
        println("DEBUG: Attempting to replace the variable.")

        // Resolve the descriptor for the variable
        val descriptors = argumentExpression.resolveMainReferenceToDescriptors()
        val variableDescriptor = descriptors.firstOrNull() as? VariableDescriptor

        if (variableDescriptor != null) {
            println("DEBUG: Resolved variable: ${variableDescriptor.name}")

            // Retrieve the PSI declaration from the descriptor
            val declaration = variableDescriptor.findPsi() as? KtVariableDeclaration

            if (declaration != null) {
                println("DEBUG: Found variable declaration for ${declaration.name}")

                // Check if the variable is in a loop or condition; skip replacement if dynamic
                if (isDynamicVariable(declaration, functionCall)) {
                    println("DEBUG: Skipping replacement due to dynamic variable changes.")
                    return
                }

                // Get the most recent value assigned to the variable
                val intValue = getLatestAssignedValue(declaration, functionCall)

                if (intValue != null) {
                    println("DEBUG: Resolved variable value: $intValue")

                    if (intValue != null && intToStringMap.containsKey(intValue)) {
                        val newValue = intToStringMap[intValue]
                        println("DEBUG: Replacing variable reference with String \"$newValue\"")

                        // Replace the variable reference in the function call with the corresponding String value
                        val newExpression = KtPsiFactory(functionCall).createExpression("\"$newValue\"")
                        argumentExpression.replace(newExpression)
                        println("DEBUG: Variable reference replacement successful!")
                    } else {
                        println("DEBUG: No corresponding String value found for Int $intValue.")
                    }
                } else {
                    println("DEBUG: Initializer is not a constant expression.")
                }
            } else {
                println("DEBUG: Failed to find the PSI declaration from the descriptor.")
            }
        } else {
            println("DEBUG: Failed to resolve the variable reference using resolveMainReferenceToDescriptors.")
        }
    }

    // Get the latest value assigned to a variable before the function call
    private fun getLatestAssignedValue(variableDeclaration: KtVariableDeclaration, functionCall: KtCallExpression): Int? {
        val blockExpression = variableDeclaration.parent
        var latestValue: Int? = null

        // Iterate through all statements in the block to find assignments to the variable
        blockExpression?.forEachDescendantOfType<KtBinaryExpression> { binaryExpression ->
            if (binaryExpression.left?.text == variableDeclaration.name && binaryExpression.right is KtConstantExpression) {
                val assignedValue = (binaryExpression.right as KtConstantExpression).text.toIntOrNull()

                // Make sure the assignment happens before the function call
                if (assignedValue != null && binaryExpression.textOffset < functionCall.textOffset) {
                    latestValue = assignedValue
                }
            }
        }

        // If no assignments were found, return the initial value of the variable
        return latestValue ?: (variableDeclaration.initializer as? KtConstantExpression)?.text?.toIntOrNull()
    }

    // Check if the variable's value changes dynamically (e.g., in a loop or conditional)
    private fun isDynamicVariable(variableDeclaration: KtVariableDeclaration, functionCall: KtCallExpression): Boolean {
        var isDynamic = false

        // Check if the variable is being modified inside loops or conditionals
        val blockExpression = variableDeclaration.parent

        // Iterate through all descendant expressions in the block and check for dynamic changes
        blockExpression?.forEachDescendantOfType<KtBinaryExpression> { binaryExpression ->
            if (binaryExpression.left?.text == variableDeclaration.name) {
                val assignedExpression = binaryExpression.right

                // Check if the assigned value is something other than a constant (e.g., a loop variable, function result)
                if (assignedExpression !is KtConstantExpression) {
                    println("DEBUG: Variable is assigned dynamically (not a constant value): ${assignedExpression?.text}")
                    isDynamic = true
                }
            }
        }

        // If no dynamic assignments are found, it's safe to replace the variable
        return isDynamic
    }
}
