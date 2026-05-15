package com.fatec.merge_skills_kmp

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.serializer.KotlinXSerializer
import io.github.jan.supabase.storage.Storage
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

@OptIn(ExperimentalSerializationApi::class)
fun createAppSupabaseClient(
    supabaseUrl: String,
    supabaseKey: String
): SupabaseClient {
    return createSupabaseClient(
        supabaseUrl = supabaseUrl,
        supabaseKey = supabaseKey
    ) {
        install(Postgrest)
        install(Auth)
        install(Storage)
        
        defaultSerializer = KotlinXSerializer(Json {
            ignoreUnknownKeys = true
            prettyPrint = true
            isLenient = true
            explicitNulls = false // Não envia campos nulos ao banco
        })
    }
}
