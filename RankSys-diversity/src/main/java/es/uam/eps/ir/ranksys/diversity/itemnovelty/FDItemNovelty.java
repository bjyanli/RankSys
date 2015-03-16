/* 
 * Copyright (C) 2014 Information Retrieval Group at Universidad Autonoma
 * de Madrid, http://ir.ii.uam.es
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.uam.eps.ir.ranksys.diversity.itemnovelty;

import es.uam.eps.ir.ranksys.core.data.RecommenderData;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import static java.lang.Math.log;
import java.util.IntSummaryStatistics;

/**
 *
 * @author Saúl Vargas (saul.vargas@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 */
public class FDItemNovelty<U, I> extends ItemNovelty<U, I> {

    private final UserFDItemNoveltyModel nov;

    public FDItemNovelty(RecommenderData<U, I, ?> recommenderData) {
        super();
        this.nov = new UserFDItemNoveltyModel(recommenderData);
    }

    @Override
    protected UserItemNoveltyModel<U, I> get(U t) {
        return nov;
    }

    @Override
    public UserItemNoveltyModel<U, I> getModel(U u) {
        return nov;
    }

    private class UserFDItemNoveltyModel implements UserItemNoveltyModel<U, I> {

        private final Object2DoubleMap<I> itemNovelty;

        public UserFDItemNoveltyModel(RecommenderData<U, I, ?> recommenderData) {
            IntSummaryStatistics stats = recommenderData.getItemsWithPreferences().mapToInt(i -> recommenderData.numUsers(i)).summaryStatistics();
            long norm = stats.getSum();
            double maxNov = -log(stats.getMin() / norm) / log(2);

            itemNovelty = new Object2DoubleOpenHashMap<>();
            itemNovelty.defaultReturnValue(maxNov);
            recommenderData.getItemsWithPreferences().forEach(i -> {
                itemNovelty.put(i, -log(recommenderData.numUsers(i) / (double) norm) / log(2));
            });
        }

        @Override
        public double novelty(I i) {
            return itemNovelty.getDouble(i);
        }

    }
}
