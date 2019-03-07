package org.hrorm.examples;

import org.hrorm.IndirectAssociationDaoBuilder;
import org.hrorm.IndirectDaoBuilder;

public class ImmutableMediaDaoBuilders {

    public static final IndirectDaoBuilder<ImmutableMovie, ImmutableMovie.ImmutableMovieBuilder> MOVIE_DAO_BUILDER =
            new IndirectDaoBuilder<>("movies", ImmutableMovie.ImmutableMovieBuilder::new, ImmutableMovie.ImmutableMovieBuilder::build)
                    .withPrimaryKey("id", "movies_sequence", ImmutableMovie::getId, ImmutableMovie.ImmutableMovieBuilder::id)
                    .withStringColumn("title", ImmutableMovie::getTitle, ImmutableMovie.ImmutableMovieBuilder::title);


    public static final IndirectDaoBuilder<ImmutableActor, ImmutableActor.ImmutableActorBuilder> ACTOR_DAO_BUILDER =
            new IndirectDaoBuilder<>("actors", ImmutableActor.ImmutableActorBuilder::new, ImmutableActor.ImmutableActorBuilder::build)
                    .withPrimaryKey("id", "actors_sequence", ImmutableActor::getId, ImmutableActor.ImmutableActorBuilder::id)
                    .withStringColumn("name", ImmutableActor::getName, ImmutableActor.ImmutableActorBuilder::name);


    public static final IndirectAssociationDaoBuilder<ImmutableActor, ImmutableActor.ImmutableActorBuilder, ImmutableMovie, ImmutableMovie.ImmutableMovieBuilder> ASSOCIATION_DAO_BUILDER =
            new IndirectAssociationDaoBuilder<ImmutableActor, ImmutableActor.ImmutableActorBuilder, ImmutableMovie, ImmutableMovie.ImmutableMovieBuilder>("actor_movie_associations", "id", "actor_movie_association_sequence")
                    .withLeft("actor_id", ACTOR_DAO_BUILDER)
                    .withRight("movie_id", MOVIE_DAO_BUILDER);
}
