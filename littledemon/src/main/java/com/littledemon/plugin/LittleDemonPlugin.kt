package com.littledemon.plugin

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * @author https://github.com/funnywolfdadada
 * @since 2020/9/5
 */
class LittleDemonPlugin: Plugin<Project> {

    override fun apply(p: Project) {
        println("apply LittleDemonPlugin $p")
        p.tasks.register("mytask", MyTask::class.java)
        p.extensions.getByType(AppExtension::class.java)
            .registerTransform(MyTransform(p))
    }

}