package org.qualet.irl.patcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

/** The folder where users drop .irlights files: {@code <gameDir>/<patchesDirName>/patches}. */
public final class PatchLibrary
{
    public static final String EXTENSION = ".irlights";

    private static final Logger LOG = LoggerFactory.getLogger("irl-patcher");

    private static volatile boolean extracted;

    private PatchLibrary()
    {}

    public static Path dir()
    {
        PatcherHost host = Patcher.host();
        Path dir = host.gameDir().resolve(host.patchesDirName()).resolve("patches");
        try
        {
            Files.createDirectories(dir);
        }
        catch (IOException ignored)
        {}
        extractBundled(dir, host);
        return dir;
    }

    public static List<Path> list()
    {
        List<Path> patches = new ArrayList<>();
        Path dir = dir();

        try (Stream<Path> stream = Files.list(dir))
        {
            stream.filter(p -> Files.isRegularFile(p) && p.getFileName().toString().toLowerCase().endsWith(EXTENSION))
                .sorted(Comparator.comparing(p -> p.getFileName().toString().toLowerCase()))
                .forEach(patches::add);
        }
        catch (IOException ignored)
        {}

        return patches;
    }

    public static void openFolder()
    {
        Patcher.host().openFolder(dir());
    }

    /** Unpacks any bundled patch (from the host) missing from {@code dir}. Runs at most
     *  once per session; a file the user already placed or edited is never overwritten. */
    private static void extractBundled(Path dir, PatcherHost host)
    {
        if (extracted)
        {
            return;
        }
        extracted = true;

        for (String name : host.bundledPatches())
        {
            Path target = dir.resolve(name);
            if (Files.exists(target))
            {
                continue;
            }
            try (InputStream in = host.openBundledPatch(name))
            {
                if (in == null)
                {
                    continue;
                }
                Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
                LOG.info("Unpacked bundled patch: {}", name);
            }
            catch (IOException e)
            {
                LOG.warn("Could not unpack bundled patch {}: {}", name, e.toString());
            }
        }
    }
}
