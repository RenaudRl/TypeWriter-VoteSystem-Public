package btc.renaud.votes.command

import com.mojang.brigadier.LiteralMessage
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import com.typewritermc.core.entries.Entry
import com.typewritermc.core.entries.Query
import com.typewritermc.engine.paper.command.dsl.ArgumentBlock
import com.typewritermc.engine.paper.command.dsl.DslCommandTree
import io.papermc.paper.command.brigadier.argument.CustomArgumentType
import java.util.concurrent.CompletableFuture
import kotlin.reflect.KClass

typealias EntryPredicate<E> = (E) -> Boolean

inline fun <S, reified E : Entry> DslCommandTree<S, *>.entryCompat(
    name: String,
    noinline filter: EntryPredicate<E> = { true },
    noinline block: ArgumentBlock<S, E> = {},
) = argument(name, CompatEntryArgumentType(E::class, filter), E::class, block)

fun <S, E : Entry> DslCommandTree<S, *>.entryCompat(
    name: String,
    klass: KClass<E>,
    filter: EntryPredicate<E> = { true },
    block: ArgumentBlock<S, E> = {},
) = argument(name, CompatEntryArgumentType(klass, filter), klass, block)

class CompatEntryArgumentType<E : Entry>(
    private val klass: KClass<E>,
    private val filter: EntryPredicate<E>,
) : CustomArgumentType.Converted<E, String> {

    override fun convert(nativeType: String): E {
        val entry = Query.findById(klass, nativeType)
            ?: Query.findByName(klass, nativeType)
            ?: throw SimpleCommandExceptionType(LiteralMessage("Could not find entry $nativeType")).create()

        if (!filter(entry)) {
            throw SimpleCommandExceptionType(LiteralMessage("Entry did not pass filter")).create()
        }

        return entry
    }

    override fun getNativeType(): ArgumentType<String> = StringArgumentType.word()

    override fun <S : Any> listSuggestions(
        context: CommandContext<S>,
        builder: SuggestionsBuilder,
    ): CompletableFuture<Suggestions> {
        val input = builder.remaining
        Query.findWhere(klass) { entry ->
            if (!filter(entry)) return@findWhere false
            entry.name.startsWith(input) || (input.length > 3 && entry.id.startsWith(input))
        }.forEach { entry ->
            builder.suggest(entry.name)
        }

        return builder.buildFuture()
    }
}

