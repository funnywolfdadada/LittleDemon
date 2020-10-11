package com.littledemon.plugin

import com.android.build.api.transform.Format
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.internal.pipeline.TransformManager
import com.google.common.collect.ImmutableSet
import org.gradle.api.Project

/**
 * @author https://github.com/funnywolfdadada
 * @since 2020/9/5
 */
open class MyTransform(val project: Project): Transform() {

    override fun transform(transformInvocation: TransformInvocation) {
        super.transform(transformInvocation)
        println("-------------------------- $name start --------------------------")
        val outputProvider = transformInvocation.outputProvider
        transformInvocation.inputs.forEach {
            it.jarInputs.forEach { jar ->
                println("${jar.name}, ${jar.contentTypes}, ${jar.status}")
                val path = jar.file.absolutePath
                if (path.endsWith(".jar")) {
                    val out = outputProvider.getContentLocation(jar.name, jar.contentTypes, jar.scopes, Format.JAR)
                    println("$path -> ${out.absolutePath}")
                }
            }
        }
        println("-------------------------- $name end --------------------------")
    }

    override fun getName(): String {
        return "MyTransform"
    }

    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> {
        return TransformManager.CONTENT_CLASS
    }

    override fun isIncremental(): Boolean {
        return true
    }

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> {
        return ImmutableSet.of(QualifiedContent.Scope.EXTERNAL_LIBRARIES)
    }
}