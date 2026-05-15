package com.fatec.estocadao.routes

import com.fatec.estocadao.models.StockItem
import com.fatec.estocadao.models.StockSummary
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.stockRoutes(supabase: SupabaseClient) {
    route("/stock") {
        
        // This must be placed before /{id} to prevent "summary" from being interpreted as an ID
        get("/summary") {
            try {
                val summary = supabase.postgrest["stock_summary"].select().decodeList<StockSummary>()
                call.respond(HttpStatusCode.OK, summary)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
            }
        }

        get {
            try {
                val stockItems = supabase.postgrest["stock_items"].select().decodeList<StockItem>()
                call.respond(HttpStatusCode.OK, stockItems)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
            }
        }
        
        get("/{id}") {
            val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Missing id"))
            try {
                val stockItem = supabase.postgrest["stock_items"].select {
                    filter {
                        eq("id", id)
                    }
                }.decodeSingleOrNull<StockItem>()
                
                if (stockItem != null) {
                    call.respond(HttpStatusCode.OK, stockItem)
                } else {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Stock item not found"))
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
            }
        }
        
        post {
            try {
                val newStockItem = call.receive<StockItem>()
                val inserted = supabase.postgrest["stock_items"].insert(newStockItem) {
                    select()
                }.decodeSingle<StockItem>()
                call.respond(HttpStatusCode.Created, inserted)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            }
        }
        
        put("/{id}") {
            val id = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Missing id"))
            try {
                val updatedStockItem = call.receive<StockItem>()
                val result = supabase.postgrest["stock_items"].update(updatedStockItem) {
                    filter {
                        eq("id", id)
                    }
                    select()
                }.decodeSingleOrNull<StockItem>()
                
                if (result != null) {
                    call.respond(HttpStatusCode.OK, result)
                } else {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Stock item not found"))
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            }
        }
        
        delete("/{id}") {
            val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Missing id"))
            try {
                supabase.postgrest["stock_items"].delete {
                    filter {
                        eq("id", id)
                    }
                }
                call.respond(HttpStatusCode.NoContent)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
            }
        }
    }
}
