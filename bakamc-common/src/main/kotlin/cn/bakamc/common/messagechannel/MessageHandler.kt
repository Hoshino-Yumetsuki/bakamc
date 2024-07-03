package cn.bakamc.common.messagechannel

typealias MessageHandler<S> = (source: S, message: MessageData) -> Unit

