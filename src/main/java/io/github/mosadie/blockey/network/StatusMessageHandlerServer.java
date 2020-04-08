package io.github.mosadie.blockey.network;

import io.github.mosadie.blockey.BlocKey;
import io.github.mosadie.blockey.common.KeyStatus;
import io.github.mosadie.blockey.server.BlocKeyServer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class StatusMessageHandlerServer implements IMessageHandler<StatusMessage, IMessage> {

    @Override
    public IMessage onMessage(StatusMessage message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().player;

        player.getServerWorld().addScheduledTask(() -> {
            BlocKey blocKey = (BlocKey) FMLCommonHandler.instance().findContainerFor(BlocKey.MODID).getMod();
            BlocKeyServer server = blocKey.getBlocKeyServer();

            blocKey.getLogger().debug("Received StatusMessage from player " + player.getName());

            for (String key : message.getKeys()) {
                KeyStatus status = message.getKeyStatus(key);
                
                String[] split = key.split(":");
                String modId = split[0];
                String keyId = split[1];
                
                if (server.hasKey(player, modId, keyId)) {
                    server.setKeyStatus(player, modId, keyId, status);
                    blocKey.getLogger().debug("Set status for key " + keyId + " from mod " + modId + " to " + status.toString());
                } else {
                    blocKey.getLogger().debug("Status update problem: Couldn't find key " + keyId + " from mod " + modId);
                }
            }
        });

        return null;
    }
}
