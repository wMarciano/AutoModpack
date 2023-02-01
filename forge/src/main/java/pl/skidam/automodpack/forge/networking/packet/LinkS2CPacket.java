package pl.skidam.automodpack.forge.networking.packet;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientLoginPacketListener;
import net.minecraftforge.network.NetworkEvent;
import pl.skidam.automodpack.AutoModpack;
import pl.skidam.automodpack.client.ModpackUpdater;

import java.io.File;
import java.util.function.Supplier;

import static pl.skidam.automodpack.AutoModpack.modpacksDir;

public class LinkS2CPacket implements Packet<ClientLoginPacketListener> {
    private final String link;
    public LinkS2CPacket(String link) {
        this.link = link;
    }

    public LinkS2CPacket(PacketByteBuf buf) {
        this.link = buf.readString();
    }

    public void write(PacketByteBuf buf) {
        buf.writeString(this.link);
    }

    public void apply(ClientLoginPacketListener listener) {
        AutoModpack.LOGGER.error("Received link packet from server! " + link);
    }

    public void apply(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            AutoModpack.LOGGER.error("Received link packet from server! " + link);
            AutoModpack.ClientLink = link;
            String modpackFileName = link.substring(link.lastIndexOf("/") + 1); // removes https:// and http://
            modpackFileName = modpackFileName.replace(":", "-"); // replaces : with -
            File modpackDir = new File(modpacksDir + File.separator + modpackFileName);
            new Thread(() -> new ModpackUpdater(link, modpackDir, true)).start();
        });
    }
}