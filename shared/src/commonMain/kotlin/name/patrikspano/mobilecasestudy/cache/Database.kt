package name.patrikspano.mobilecasestudy.cache

import name.patrikspano.mobilecasestudy.entity.CoinData
import name.patrikspano.mobilecasestudy.entity.SearchCoin
import name.patrikspano.mobilecasestudy.entity.TrendingCoin
import name.patrikspano.mobilecasestudy.entity.SparklineData

internal class Database(databaseDriverFactory: DatabaseDriverFactory) {
    private val database = AppDatabase(databaseDriverFactory.createDriver())
    private val dbQuery = database.appDatabaseQueries

    // CoinData operations
    internal fun getAllCoinData(): List<CoinData> {
        return dbQuery.selectAllCoinData(::mapCoinData).executeAsList()
    }

    private fun mapCoinData(
        id: String,
        name: String,
        symbol: String,
        image: String,
        current_price: Double,
        market_cap: Long,
        price_change_percentage_24h: Double?,
        sparkline: String?,
        is_favorite: Long
    ): CoinData {
        val sparklineData = SparklineData(
            price = sparkline?.split(",")?.mapNotNull { it.toDoubleOrNull() } ?: emptyList()
        )
        return CoinData(
            id = id,
            name = name,
            symbol = symbol,
            image = image,
            currentPrice = current_price,
            marketCap = market_cap,
            priceChangePercentage24h = price_change_percentage_24h ?: 0.0,
            sparkline = sparklineData,
            isFavorite = is_favorite == 1L
        )
    }

    internal fun clearAndCreateCoinData(coins: List<CoinData>) {
        dbQuery.transaction {
            dbQuery.removeAllCoinData()
            coins.forEach { coin ->
                println("Inserting coin with favorite status: ${coin.id} - ${coin.isFavorite}")
                dbQuery.insertCoinData(
                    id = coin.id,
                    name = coin.name,
                    symbol = coin.symbol,
                    image = coin.image,
                    current_price = coin.currentPrice,
                    market_cap = coin.marketCap,
                    price_change_percentage_24h = coin.priceChangePercentage24h,
                    sparkline = coin.sparkline.price.joinToString(","),
                    is_favorite = if (coin.isFavorite) 1 else 0
                )
            }
        }
    }

    internal fun updateFavoriteStatus(coin: CoinData) {
        dbQuery.updateCoinData(
            is_favorite = if (coin.isFavorite) 1 else 0,
            id = coin.id
        )
    }

    // SearchCoin operations
    internal fun getAllSearchCoins(): List<SearchCoin> {
        return dbQuery.selectAllSearchCoins(::mapSearchCoin).executeAsList()
    }

    private fun mapSearchCoin(
        id: String,
        name: String,
        api_symbol: String,
        symbol: String,
        market_cap_rank: Long,
        thumb: String,
        large: String
    ): SearchCoin {
        return SearchCoin(
            id = id,
            name = name,
            apiSymbol = api_symbol,
            symbol = symbol,
            marketCapRank = market_cap_rank,
            thumb = thumb,
            large = large
        )
    }

    internal fun clearAndCreateSearchCoins(coins: List<SearchCoin>) {
        dbQuery.transaction {
            dbQuery.removeAllSearchCoins()
            coins.forEach { coin ->
                dbQuery.insertSearchCoin(
                    id = coin.id,
                    name = coin.name,
                    api_symbol = coin.apiSymbol,
                    symbol = coin.symbol,
                    market_cap_rank = coin.marketCapRank,
                    thumb = coin.thumb,
                    large = coin.large
                )
            }
        }
    }

    // TrendingCoin operations
    internal fun getAllTrendingCoins(): List<TrendingCoin> {
        return dbQuery.selectAllTrendingCoins(::mapTrendingCoin).executeAsList()
    }

    private fun mapTrendingCoin(
        id: String,
        coin_id: Long,
        name: String,
        symbol: String,
        market_cap_rank: Long?,
        thumb: String?,
        small: String?,
        large: String?,
        slug: String?,
        price_btc: Double?,
        score: Long
    ): TrendingCoin {
        return TrendingCoin(
            id = id,
            coinId = coin_id,
            name = name,
            symbol = symbol,
            marketCapRank = market_cap_rank ?: 0,
            thumb = thumb.orEmpty(),
            small = small.orEmpty(),
            large = large.orEmpty(),
            slug = slug.orEmpty(),
            priceBtc = price_btc ?: 0.0,
            score = score
        )
    }

    internal fun clearAndCreateTrendingCoins(coins: List<TrendingCoin>) {
        dbQuery.transaction {
            dbQuery.removeAllTrendingCoins()
            coins.forEach { coin ->
                dbQuery.insertTrendingCoin(
                    id = coin.id,
                    coin_id = coin.coinId,
                    name = coin.name,
                    symbol = coin.symbol,
                    market_cap_rank = coin.marketCapRank,
                    thumb = coin.thumb,
                    small = coin.small,
                    large = coin.large,
                    slug = coin.slug,
                    price_btc = coin.priceBtc,
                    score = coin.score
                )
            }
        }
    }
}