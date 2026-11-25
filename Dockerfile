# ChiyoMixin on itzg/minecraft-server (https://github.com/itzg/docker-minecraft-server).
# The jar is built beforehand (./gradlew createChiyoMixinJar); this only packages it.
# All of itzg's settings (EULA, MEMORY, JVM_OPTS, OPS, ...) work as usual.
FROM itzg/minecraft-server:java21

COPY build/libs/ChiyoMixin-paperclip-*.jar /opt/chiyomixin/chiyomixin.jar

ENV TYPE=CUSTOM \
    CUSTOM_SERVER=/opt/chiyomixin/chiyomixin.jar
