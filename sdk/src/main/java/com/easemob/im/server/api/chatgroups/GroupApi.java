package com.easemob.im.server.api.chatgroups;

import com.easemob.im.server.api.Context;
import com.easemob.im.server.api.chatgroups.detail.GroupDetails;
import com.easemob.im.server.api.chatgroups.update.GroupUpdate;
import com.easemob.im.server.api.chatgroups.update.GroupUpdateRequest;
import com.easemob.im.server.model.EMGroupDetails;
import reactor.core.publisher.Mono;

import java.util.function.Function;

public class GroupApi {

    private Context context;

    private String groupId;

    public GroupApi(Context context, String groupId) {
        this.context = context;
        this.groupId = groupId;
    }

    /**
     * Get group detail.
     *
     * To get group details,
     * <pre>{@code
     *      EMService service;
     *      EMGroupDetails details = service.group("1").getDetail().block();
     * }</pre>
     *
     * @return A {@code Mono} emits {@code EMGroupDetail} on success.
     */
    public Mono<EMGroupDetails> detail() {
        return GroupDetails.execute(this.context, this.groupId);
    }

    /**
     * Update group settings.
     * <p>
     * To update max members to 100,
     * <pre>{@code
     *     EMService service;
     *     service.group("111").updateSettings(settings -> settings.maxMembers(100)).block();
     * }</pre>
     *
     * @param customizer update request customizer
     * @return A {@code Mono} complete if successful.
     */
    public Mono<Void> update(Function<GroupUpdateRequest, GroupUpdateRequest> customizer) {
        return new GroupUpdate(this.context, this.groupId, customizer.apply(new GroupUpdateRequest()))
            .execute();
    }

}