import sys
content = open('tools/patch_waze_route.py').read()
cameras_method = r'''
.method public static publishCameras(Landroid/content/Context;Lcom/waze/jni/protos/alerters/NativeAlertRepositoryUpdate;)V
    .locals 7

    if-eqz p0, :end
    if-eqz p1, :end

    invoke-virtual {p1}, Lcom/waze/jni/protos/alerters/NativeAlertRepositoryUpdate;->getDescriptorsCount()I
    move-result v0

    const/4 v1, 0x0
    :loop_start
    if-ge v1, v0, :clear_camera

    invoke-virtual {p1, v1}, Lcom/waze/jni/protos/alerters/NativeAlertRepositoryUpdate;->getDescriptors(I)Lcom/waze/jni/protos/alerters/NativeAlertDescriptor;
    move-result-object v2
    if-eqz v2, :loop_next

    invoke-virtual {v2}, Lcom/waze/jni/protos/alerters/NativeAlertDescriptor;->getInfo()Lcom/waze/jni/protos/alerters/AlerterInfo;
    move-result-object v3
    if-eqz v3, :loop_next

    invoke-virtual {v3}, Lcom/waze/jni/protos/alerters/AlerterInfo;->getTypeValue()I
    move-result v4
    const/16 v5, 0xa
    if-eq v4, v5, :found_camera

    :loop_next
    add-int/lit8 v1, v1, 0x1
    goto :loop_start

    :found_camera
    invoke-virtual {v2}, Lcom/waze/jni/protos/alerters/NativeAlertDescriptor;->getAlertId()Lcom/waze/jni/protos/alerters/AlerterId;
    move-result-object v2
    if-eqz v2, :loop_next

    invoke-virtual {v2}, Lcom/waze/jni/protos/alerters/AlerterId;->getUuid()Ljava/lang/String;
    move-result-object v2

    invoke-virtual {v3}, Lcom/waze/jni/protos/alerters/AlerterInfo;->getDistanceString()Ljava/lang/String;
    move-result-object v3

    new-instance v5, Landroid/content/Intent;
    const-string v6, "com.yandex.ROADCAMERA"
    invoke-direct {v5, v6}, Landroid/content/Intent;-><init>(Ljava/lang/String;)V

    const-string v6, "com.g992.anhud"
    invoke-virtual {v5, v6}, Landroid/content/Intent;->setPackage(Ljava/lang/String;)Landroid/content/Intent;

    const-string v6, "camera_id"
    invoke-virtual {v5, v6, v2}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;

    const-string v6, "distance_text"
    invoke-virtual {v5, v6, v3}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;

    invoke-virtual {p0, v5}, Landroid/content/Context;->sendBroadcast(Landroid/content/Intent;)V
    goto :end

    :clear_camera
    new-instance v5, Landroid/content/Intent;
    const-string v6, "com.yandex.ROADCAMERA"
    invoke-direct {v5, v6}, Landroid/content/Intent;-><init>(Ljava/lang/String;)V

    const-string v6, "com.g992.anhud"
    invoke-virtual {v5, v6}, Landroid/content/Intent;->setPackage(Ljava/lang/String;)Landroid/content/Intent;

    const-string v6, "camera_id"
    const-string v2, ""
    invoke-virtual {v5, v6, v2}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;

    invoke-virtual {p0, v5}, Landroid/content/Context;->sendBroadcast(Landroid/content/Intent;)V

    :end
    return-void
.end method
'''
content = content.replace("    return-void\n.end method\n'''\n\nROUTE_HOOK", "    return-void\n.end method\n" + cameras_method + "\n'''\n\nROUTE_HOOK")
open('tools/patch_waze_route.py', 'w').write(content)
