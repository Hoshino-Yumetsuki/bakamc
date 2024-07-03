package cn.bakamc.common.messagechannel

data class Message<S>(val channel: MessageChannel, val messageHandler: MessageHandler<S>)