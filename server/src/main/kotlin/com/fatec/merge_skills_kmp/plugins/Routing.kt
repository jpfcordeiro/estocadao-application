package com.fatec.merge_skills_kmp.plugins

import com.fatec.estocadao.routes.productRoutes
import com.fatec.estocadao.routes.stockRoutes
import io.github.jan.supabase.SupabaseClient
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(supabase: SupabaseClient?) {
    routing {
        get("/") {
            call.respondText("Estocadão API is running!")
        }

        if (supabase != null) {
            productRoutes(supabase)
            stockRoutes(supabase)
        } else {
            get("/error") {
                call.respondText("Supabase client is not configured. Check your environment variables.")
            }
        }
    }
}
