package team.torka.thaumicrecords.attachment;

import team.torka.thaumicrecords.api.aspect.AspectList;

public record AspectListAttachment(AspectList aspects) {
    public static final AspectListAttachment DEFAULT = defaultAspectListAttachment();


    public static AspectListAttachment defaultAspectListAttachment() {
        return new AspectListAttachment(new AspectList());
    }
}