package xfacthd.contex.client.util;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceMetadata;
import net.neoforged.fml.loading.FMLPaths;
import xfacthd.contex.api.model.ModelUtils;
import xfacthd.contex.api.texture.Border;
import xfacthd.contex.client.texture.ConTexSpriteSupplier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ConTexCommand
{
    private static final DynamicCommandExceptionType EX_NO_SUCH_TEXTURE = new DynamicCommandExceptionType(
            tex -> Component.translatable("msg.contex.gen_ctm_tex.no_such_texture", tex)
    );
    private static final SimpleCommandExceptionType EX_INVALID_CORNER_SYNTH = new SimpleCommandExceptionType(
            Component.translatable("msg.contex.gen_ctm_tex.invalid_corner_synth")
    );
    private static final SimpleCommandExceptionType EX_GEN_FAILED = new SimpleCommandExceptionType(
            Component.translatable("msg.contex.gen_ctm_tex.gen_failed")
    );
    private static final DynamicCommandExceptionType EX_EXPORT_FAILED = new DynamicCommandExceptionType(
            msg -> Component.translatable("msg.contex.gen_ctm_tex.export_failed", msg)
    );
    private static final String MSG_GEN_SUCCESS = "msg.contex.gen_ctm_tex.success";

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        dispatcher.register(
                Commands.literal("contex")
                        .then(Commands.literal("gen_ctm_tex")
                                .then(Commands.argument("src_texture", ResourceLocationArgument.id())
                                        .then(Commands.argument("border", IntegerArgumentType.integer(1))
                                                .executes(ConTexCommand::generateTextureSimpleBorder)
                                                .then(Commands.argument("mirror_parallel", BoolArgumentType.bool())
                                                        .then(Commands.argument("mirror_perpendicular", BoolArgumentType.bool())
                                                                .then(Commands.argument("copy_from_opposite_edge", BoolArgumentType.bool())
                                                                        .then(Commands.argument("synthesize_inner_corners", BoolArgumentType.bool())
                                                                                .executes(ConTexCommand::generateTextureSimpleBorderMirror)
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                        .then(Commands.argument("border_left", IntegerArgumentType.integer(1))
                                                .then(Commands.argument("border_right", IntegerArgumentType.integer(1))
                                                        .then(Commands.argument("border_top", IntegerArgumentType.integer(1))
                                                                .then(Commands.argument("border_bottom", IntegerArgumentType.integer(1))
                                                                        .executes(ConTexCommand::generateTextureFullBorder)
                                                                        .then(Commands.argument("mirror_parallel", BoolArgumentType.bool())
                                                                                .then(Commands.argument("mirror_perpendicular", BoolArgumentType.bool())
                                                                                        .then(Commands.argument("copy_from_opposite_edge", BoolArgumentType.bool())
                                                                                                .then(Commands.argument("synthesize_inner_corners", BoolArgumentType.bool())
                                                                                                        .executes(ConTexCommand::generateTextureFullBorderMirror)
                                                                                                )
                                                                                        )
                                                                                )
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
        );
    }

    private static int generateTextureSimpleBorder(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException
    {
        CommandSourceStack source = ctx.getSource();
        ResourceLocation texture = ResourceLocationArgument.getId(ctx, "src_texture");
        int border = IntegerArgumentType.getInteger(ctx, "border");
        return generateTexture(source, texture, border, border, border, border, false, false, false, false);
    }

    private static int generateTextureSimpleBorderMirror(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException
    {
        CommandSourceStack source = ctx.getSource();
        ResourceLocation texture = ResourceLocationArgument.getId(ctx, "src_texture");
        int border = IntegerArgumentType.getInteger(ctx, "border");
        boolean mirrorParallel = BoolArgumentType.getBool(ctx, "mirror_parallel");
        boolean mirrorPerpendicular = BoolArgumentType.getBool(ctx, "mirror_perpendicular");
        boolean copyFromOppositeEdge = BoolArgumentType.getBool(ctx, "copy_from_opposite_edge");
        boolean synthesizeInnerCorners = BoolArgumentType.getBool(ctx, "synthesize_inner_corners");
        return generateTexture(source, texture, border, border, border, border, mirrorParallel, mirrorPerpendicular, copyFromOppositeEdge, synthesizeInnerCorners);
    }

    private static int generateTextureFullBorder(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException
    {
        CommandSourceStack source = ctx.getSource();
        ResourceLocation texture = ResourceLocationArgument.getId(ctx, "src_texture");
        int borderLeft = IntegerArgumentType.getInteger(ctx, "border_left");
        int borderRight = IntegerArgumentType.getInteger(ctx, "border_right");
        int borderTop = IntegerArgumentType.getInteger(ctx, "border_top");
        int borderBottom = IntegerArgumentType.getInteger(ctx, "border_bottom");
        return generateTexture(source, texture, borderLeft, borderRight, borderTop, borderBottom, false, false, false, false);
    }

    private static int generateTextureFullBorderMirror(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException
    {
        CommandSourceStack source = ctx.getSource();
        ResourceLocation texture = ResourceLocationArgument.getId(ctx, "src_texture");
        int borderLeft = IntegerArgumentType.getInteger(ctx, "border_left");
        int borderRight = IntegerArgumentType.getInteger(ctx, "border_right");
        int borderTop = IntegerArgumentType.getInteger(ctx, "border_top");
        int borderBottom = IntegerArgumentType.getInteger(ctx, "border_bottom");
        boolean mirrorParallel = BoolArgumentType.getBool(ctx, "mirror_parallel");
        boolean mirrorPerpendicular = BoolArgumentType.getBool(ctx, "mirror_perpendicular");
        boolean copyFromOppositeEdge = BoolArgumentType.getBool(ctx, "copy_from_opposite_edge");
        boolean synthesizeInnerCorners = BoolArgumentType.getBool(ctx, "synthesize_inner_corners");
        return generateTexture(source, texture, borderLeft, borderRight, borderTop, borderBottom, mirrorParallel, mirrorPerpendicular, copyFromOppositeEdge, synthesizeInnerCorners);
    }

    private static int generateTexture(
            CommandSourceStack source,
            ResourceLocation texture,
            int borderLeft,
            int borderRight,
            int borderTop,
            int borderBottom,
            boolean mirrorParallel,
            boolean mirrorPerpendicular,
            boolean copyFromOppositeEdge,
            boolean synthesizeInnerCorners
    ) throws CommandSyntaxException
    {
        SpriteContents srcSprite = ModelUtils.getSprite(texture).contents();
        if (srcSprite.name().equals(MissingTextureAtlasSprite.getLocation()))
        {
            throw EX_NO_SUCH_TEXTURE.create(texture);
        }

        NativeImage srcImage = srcSprite.getOriginalImage();
        ResourceMetadata metadata = srcSprite.metadata();
        ResourceLocation outLoc = texture.withSuffix("_ctm");
        Border border = new Border(borderLeft, borderTop, borderRight, borderBottom, mirrorParallel, mirrorPerpendicular, copyFromOppositeEdge, synthesizeInnerCorners);

        if (synthesizeInnerCorners && !border.canSynthesizeCorners())
        {
            throw EX_INVALID_CORNER_SYNTH.create();
        }

        SpriteContents ctmSprite = ConTexSpriteSupplier.createTexture(texture, outLoc, srcImage, metadata, border);
        if (ctmSprite == null)
        {
            throw EX_GEN_FAILED.create();
        }

        int lastSlash = outLoc.getPath().lastIndexOf('/');
        String fileName = outLoc.getPath().substring(lastSlash + 1) + ".png";
        Path exportPath = FMLPaths.GAMEDIR.get()
                .resolve("contex/export/")
                .resolve(fileName);

        try
        {
            Files.createDirectories(exportPath.getParent());
            ctmSprite.getOriginalImage().writeToFile(exportPath);
        }
        catch (IOException e)
        {
            throw EX_EXPORT_FAILED.create(e.toString());
        }

        source.sendSuccess(() ->
        {
            Component path = Component.literal(exportPath.toAbsolutePath().normalize().toString())
                    .withStyle(style -> style.withClickEvent(new ClickEvent.OpenFile(exportPath)));
            return Component.translatable(MSG_GEN_SUCCESS, path);
        }, false);
        return Command.SINGLE_SUCCESS;
    }

    private ConTexCommand() { }
}
