package me.capcom.smsgateway.modules.line

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val lineModule = module {
    singleOf(::LineMessagingService)
}
