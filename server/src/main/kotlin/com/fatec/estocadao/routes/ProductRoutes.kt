package com.fatec.estocadao.routes

import com.fatec.estocadao.models.Product
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.productRoutes(supabase: SupabaseClient) {
    route("/products") {
        
        get {
            try {
                val products = supabase.postgrest["products"].select().decodeList<Product>()
                call.respond(HttpStatusCode.OK, products)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
            }
        }
        
        get("/{id}") {
            val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Missing id"))
            try {
                val product = supabase.postgrest["products"].select {
                    filter {
                        eq("id", id)
                    }
                }.decodeSingleOrNull<Product>()
                
                if (product != null) {
                    call.respond(HttpStatusCode.OK, product)
                } else {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Product not found"))
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
            }
        }
        
        post {
            try {
                val newProduct = call.receive<Product>()
                // In Supabase, usually we shouldn't insert the ID if it's generated, but Postgrest allows omitting it.
                // It's safer to just insert and return the inserted row.
                val inserted = supabase.postgrest["products"].insert(newProduct) {
                    select()
                }.decodeSingle<Product>()
                call.respond(HttpStatusCode.Created, inserted)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            }
        }
        
        put("/{id}") {
            val id = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Missing id"))
            try {
                val updatedProduct = call.receive<Product>()
                val result = supabase.postgrest["products"].update(updatedProduct) {
                    filter {
                        eq("id", id)
                    }
                    select()
                }.decodeSingleOrNull<Product>()
                
                if (result != null) {
                    call.respond(HttpStatusCode.OK, result)
                } else {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Product not found"))
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            }
        }
        
        delete("/{id}") {
            val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Missing id"))
            try {
                // If cascade delete is configured in the DB, this will delete stock_items as well.
                supabase.postgrest["products"].delete {
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
